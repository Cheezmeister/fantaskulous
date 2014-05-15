package com.luchenlabs.fantaskulous.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.luchenlabs.fantaskulous.test.EverythingTest;
import com.luchenlabs.fantaskulous.test.TaskListTest;
import com.luchenlabs.fantaskulous.test.TaskTest;

@RunWith(Suite.class)
@SuiteClasses({ TaskListTest.class, TaskTest.class, EverythingTest.class })
public class AllTests {
    // Why is this even a class I don't even
}
