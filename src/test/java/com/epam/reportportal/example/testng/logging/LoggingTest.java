/*
 * Copyright 2022 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.reportportal.example.testng.logging;

import com.epam.reportportal.listeners.LogLevel;
import com.epam.reportportal.message.ReportPortalMessage;
import com.epam.reportportal.message.TypeAwareByteSource;
import com.epam.reportportal.service.ReportPortal;
import com.epam.reportportal.utils.files.Utils;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * Different logging types and options example test
 */
public class LoggingTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingTest.class);
	private static final Logger BINARY_LOGGER = LoggerFactory.getLogger("binary_data_logger");

	@Test
	public void logCss() {
		File file = new File("src/test/resources/files/css.css");
		BINARY_LOGGER.info("RP_MESSAGE#FILE#{}#{}", file.getAbsolutePath(), "I'm logging CSS");
	}

	@Test
	public void logHtml() throws IOException {
		File file = new File("files/html.html");
		TypeAwareByteSource byteSource = Utils.getFile(file);
		BINARY_LOGGER.info("RP_MESSAGE#BASE64#{}#{}",
				Base64.getEncoder().encodeToString(byteSource.read()),
				"I'm logging HTML"
		);
	}

	@Test
	public void logPdf() {
		File file = new File("src/test/resources/files/test.pdf");
		ReportPortal.emitLog("I'm logging PDF", LogLevel.INFO.name(), Calendar.getInstance().getTime(), file);
	}

	@Test
	public void logZip() {
		File file = new File("src/test/resources/files/demo.zip");
		ReportPortal.emitLaunchLog("I'm logging ZIP", LogLevel.WARN.name(), Calendar.getInstance().getTime(), file);
	}

	@Test
	public void logJavascript() throws IOException {
		File file = new File("files/javascript.js");
		ReportPortalMessage message = new ReportPortalMessage(file, "I'm logging JS");
		ReportPortal.emitLog(message, LogLevel.DEBUG.name(), Calendar.getInstance().getTime());
	}

	@Test
	public void logPhp() throws IOException {
		File file = new File("files/php.php");
		ReportPortalMessage message = new ReportPortalMessage(Utils.getFile(file), "I'm logging php");
		ReportPortal.emitLog(message, LogLevel.ERROR.name(), Calendar.getInstance().getTime());
	}

	@Test
	public void logPlain() throws IOException {
		File file = new File("files/plain.txt");
		ReportPortalMessage message = new ReportPortalMessage(Utils.getFile(file), "text/plain", "I'm logging txt");
		ReportPortal.emitLog(message, LogLevel.INFO.name(), Calendar.getInstance().getTime());
	}

	@Test(timeOut = 500)
	public void logInChildThread() {
		LOGGER.info("I'm logging in a test with timeout");
	}

	@Test
	public void logInAwaitilityThread() {
		AtomicInteger counter = new AtomicInteger();
		Awaitility.await("Logging inside Awaitility")
				.atMost(Duration.of(1, ChronoUnit.SECONDS))
				.pollDelay(Duration.ZERO)
				.pollInterval(Duration.ofMillis(200))
				.until(() -> {
					int count = counter.incrementAndGet();
					LOGGER.info("Inside Awaitility " + count);
					return count;
				}, greaterThanOrEqualTo(4));
	}
}
