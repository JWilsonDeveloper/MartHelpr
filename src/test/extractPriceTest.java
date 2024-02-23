package test;

import org.junit.jupiter.api.Test;
import utility.ExtractPrice;

import static org.junit.jupiter.api.Assertions.*;

class extractPriceTest {

    @Test
    void test() {
        String string1 = "Chicken Thighs $3.99";
        String string2 = "Hot Sauce $3";
        String string3 = "Salted Cashews $6.0";
        String string4 = "Pizza Dough $2.999";
        String string5 = "Tomato Soup";
        String string6 = "Apples $3.29 $4.19";
        String string7 = "Coffee 10.35";
        String string8 = "Bubble Gum -0.99";
        String string9 = "Whole Wheat Bread 2.0 $1.111 2.79";

        assertAll(
            () -> assertEquals("3.99", ExtractPrice.extractPrice(string1), "A price with two decimal places should be extracted and returned"),
            () -> assertEquals(null, ExtractPrice.extractPrice(string2), "Exactly two decimal places are required"),
            () -> assertEquals(null, ExtractPrice.extractPrice(string3), "Exactly two decimal places are required"),
            () -> assertEquals(null, ExtractPrice.extractPrice(string4), "Exactly two decimal places are required"),
            () -> assertEquals(null, ExtractPrice.extractPrice(string5), "A string with no numbers will return null"),
            () -> assertEquals(null, ExtractPrice.extractPrice(string6), "A string with multiple correctly formatted prices will return null"),
            () -> assertEquals("10.35", ExtractPrice.extractPrice(string7), "Dollar signs are not required"),
            () -> assertEquals("0.99", ExtractPrice.extractPrice(string8), "Minus signs will be ignored"),
            () -> assertEquals("2.79", ExtractPrice.extractPrice(string9), "If there is only one correctly formatted price, it will be returned")
        );
    }
}