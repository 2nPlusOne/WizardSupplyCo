package com.estore.api.estoreapi.model;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.HashSet;

/**
 * The unit test suite for the Description class
 * 
 * @author Noah Landis
 */
@Tag("Model-tier")
public class DescriptionTest {
    @Test
    public void testConstructorSummaryAndTags() {
        // Setup
        String expectedSummary = "A REALLY Fast Broom!";
        HashSet<String> expectedTags = new HashSet<>();
        expectedTags.add("Broom");
        expectedTags.add("Fast");
        expectedTags.add("Wood");

        // Invoke
        Description description = new Description(expectedSummary, expectedTags);

        // Analyze
        assertEquals(expectedSummary, description.getSummary());
        assertEquals(expectedTags, description.getTags());
    }


    @Test
    public void testConstructorSummaryOnly() {
        // Setup
        String expectedSummary = "A REALLY Fast Broom!";

        // Invoke
        Description description = new Description(expectedSummary);

        // Analyze
        assertEquals(expectedSummary, description.getSummary());
        assertNotNull(description.getTags());
        assertTrue(description.getTags().isEmpty());
    }

    @Test
    public void testSetSummary() {
        // Setup
        String initialSummary = "A REALLY Fast Broom!";
        String expectedSummary = "An EVEN FASTER Broom!";
        Description description = new Description(initialSummary);

        // Invoke
        description.setSummary(expectedSummary);
        

        // Analyze
        assertEquals(expectedSummary, description.getSummary());
    }

    @Test
    public void testGetTags() {
        // Setup
        String expectedSummary = "A REALLY Fast Broom!";
        HashSet<String> expectedTags = new HashSet<>();
        Description description = new Description(expectedSummary, expectedTags);
        expectedTags.add("Broom");
        expectedTags.add("Fast");
        expectedTags.add("Wood");

        // Invoke
        HashSet<String> returnedTags = description.getTags();

        // Analyze
        assertEquals(expectedTags, returnedTags);
    }

    @Test
    public void testAddTag() {
        // Setup
        String summary = "A REALLY Fast Broom!";
        Description description = new Description(summary);
        int expectedTagCount = 1;

        // Invoke
        description.addTag("New Tag");
        HashSet<String> returnedTags = description.getTags();

        // Analyze
        assertEquals(expectedTagCount, returnedTags.size());
        assertTrue(returnedTags.contains("New Tag"));
    }

    @Test
    public void removeTag() {
        // Setupv
        Description description = new Description("A REALLY Fast Broom!");
        String tag = "New Tag";
        description.addTag(tag);
        int expectedTagCount = 0;
        HashSet<String> expectedReturnedTags = new HashSet<>();

        // Invoke
        description.removeTag(tag);
        HashSet<String> returnedTags = description.getTags();

        // Analyze
        assertEquals(expectedReturnedTags, returnedTags);
        assertEquals(expectedTagCount, returnedTags.size());
        assertFalse(returnedTags.contains(tag));

    }


    @Test
    public void testToString() {
        // Setup
        String summary = "A REALLY Fast Broom!";
        Description description = new Description(summary);
        description.addTag("Wood");
        description.addTag("Broom");
        description.addTag("Flying");
        String expectedString = String.format(Description.STRING_FORMAT,summary, description.getTags());

        // Invoke
        String actualString = description.toString();

        // Analyze
        assertEquals(expectedString, actualString);
    }
}