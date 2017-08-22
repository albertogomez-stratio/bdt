/*
 * Copyright (c) 2014. Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software is licensed under the Apache Licence 2.0. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the terms of the License for more details.
 * SPDX-License-Identifier: Artistic-2.0
 */

package com.stratio.qa.aspects;

import com.stratio.qa.assertions.SeleniumAssert;
import com.stratio.qa.specs.BaseGSpec;
import com.stratio.qa.specs.CommonG;
import com.stratio.qa.specs.ThenGSpec;
import com.stratio.qa.utils.PreviousWebElements;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;

@Aspect
public class SeleniumAspect extends BaseGSpec {

    private final Logger logger = LoggerFactory.getLogger(this.getClass()
            .getCanonicalName());

    @Pointcut("call(* SeleniumAssert.*(..))"
            + " || call(* org.openqa.selenium.*.click(..))"
            + " || call(* org.openqa.selenium.*.findElement(..))")
    protected void exceptionCallPointcut() {
    }

    /**
     * If an exception has thrown by selenium, this methods save a screen
     * capture.
     *
     * @param pjp ProceedingJoinPoint
     * @return Object object
     * @throws Throwable exception
     */
    @Around(value = "exceptionCallPointcut()")
    public Object aroundExceptionCalls(ProceedingJoinPoint pjp)
            throws Throwable {
        Object retVal = null;
        try {
            retVal = pjp.proceed();
            return retVal;
        } catch (Throwable ex) {
            WebDriver driver = null;
            if (ex instanceof WebDriverException) {
                logger.info("Got a selenium exception");
                if (!(pjp.getThis() instanceof WebDriver)) {
                    throw ex;
                }
                driver = (WebDriver) pjp.getThis();
            } else if ((pjp.getTarget() instanceof SeleniumAssert)
                    && (ex instanceof AssertionError)) {
                logger.info("Got a SeleniumAssert response");
                SeleniumAssert as = (SeleniumAssert) pjp.getTarget();
                Class<?> c = as.getClass().getSuperclass();
                Field actual = c.getDeclaredField("actual");
                actual.setAccessible(true);
                Object realActual = actual.get(as);

                if (realActual instanceof WebDriver) {
                    driver = (WebDriver) actual.get(as);
                } else if (realActual instanceof ArrayList) {
                    if (((ArrayList) realActual).get(0) instanceof RemoteWebElement) {
                        driver = ((RemoteWebElement) ((ArrayList) realActual).get(0)).getWrappedDriver();
                    }
                } else if ((realActual instanceof PreviousWebElements) ||
                        (realActual instanceof Boolean) ||
                        (realActual instanceof String) ||
                        (realActual == null)) {
                    driver = ((CommonG) ((SeleniumAssert) pjp.getTarget()).getCommonspec()).getDriver();
                } else if (realActual instanceof RemoteWebElement) {
                    driver = ((RemoteWebElement) actual.get(as)).getWrappedDriver();
                }
            }
            if (driver != null) {
                logger.info("Trying to capture screenshots...");
                CommonG common = null;
                if ((pjp.getThis() instanceof ThenGSpec) && (((ThenGSpec) pjp.getThis()).getCommonSpec() != null)) {
                    common = ((ThenGSpec) pjp.getThis()).getCommonSpec();
                } else if ((pjp.getTarget() instanceof SeleniumAssert) && ((SeleniumAssert) pjp.getTarget()).getCommonspec() != null) {
                    common = ((CommonG) ((SeleniumAssert) pjp.getTarget()).getCommonspec());
                } else {
                    logger.info("Got no Selenium driver to capture a screen");
                    throw ex;
                }
                common.captureEvidence(driver, "framehtmlSource", "assert");
                common.captureEvidence(driver, "htmlSource", "assert");
                common.captureEvidence(driver, "screenCapture", "assert");
                logger.info("Screenshots are available at target/executions");
            } else {
                logger.info("Got no Selenium driver to capture a screen");
            }
            throw ex;
        }
    }
}