/*
 * Copyright (c) 2014. Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software is licensed under the Apache Licence 2.0. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the terms of the License for more details.
 * SPDX-License-Identifier: Artistic-2.0
 */

package com.stratio.qa.assertions;

import com.mongodb.DBObject;
import com.stratio.qa.specs.CommonG;
import com.stratio.qa.utils.PreviousWebElements;
import cucumber.api.DataTable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class Assertions extends org.assertj.core.api.Assertions {
    /**
     * Check if two WebElements are equals.
     *
     * @param actual actual webElement
     * @return SeleniumAssert assertion
     */
    public static SeleniumAssert assertThat(WebElement actual) {
        return new SeleniumAssert(actual);
    }

    public static SeleniumAssert assertThat(PreviousWebElements actualList) {
        return new SeleniumAssert(actualList.getPreviousWebElements());
    }


    /**
     * Check if two WebDrivers are equals.
     *
     * @param actual webElement
     * @return SeleniumAssert assert
     */
    public static SeleniumAssert assertThat(WebDriver actual) {
        return new SeleniumAssert(actual);
    }


    public static SeleniumAssert assertThat(CommonG common, WebDriver actual) {
        return new SeleniumAssert(common, actual);
    }

    public static SeleniumAssert assertThat(CommonG common, WebElement actual) {
        return new SeleniumAssert(common, actual);
    }

    public static SeleniumAssert assertThat(CommonG common, List<WebElement> actual) {
        return new SeleniumAssert(common, actual);
    }

    public static SeleniumAssert assertThat(CommonG common, PreviousWebElements actual) {
        return new SeleniumAssert(common, actual);
    }

    public static SeleniumAssert assertThat(CommonG common, boolean actual) {
        return new SeleniumAssert(common, actual);
    }

    public static SeleniumAssert assertThat(CommonG common, String actual) {
        return new SeleniumAssert(common, actual);
    }

    public static DBObjectsAssert assertThat(DataTable data, ArrayList<DBObject> actual) {
        return new DBObjectsAssert(actual);
    }

}
