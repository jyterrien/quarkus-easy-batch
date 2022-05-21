/*
 * The MIT License
 *
 *  Copyright (c) 2021, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package org.jeasy.batch.tutorials.intermediate.extract;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jeasy.batch.core.job.Job;
import org.jeasy.batch.core.job.JobBuilder;
import org.jeasy.batch.core.job.JobExecutor;
import org.jeasy.batch.core.job.JobReport;
import org.jeasy.batch.core.writer.FileRecordWriter;
import org.jeasy.batch.flatfile.DelimitedRecordMarshaller;
import org.jeasy.batch.jdbc.JdbcRecordMapper;
import org.jeasy.batch.jdbc.JdbcRecordReader;
import org.jeasy.batch.tutorials.common.Tweet;

import io.agroal.api.AgroalDataSource;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;

/**
 * Main class to run the JDBC data export tutorial.
 *
 * The goal is to read tweets from a relational database and export them to a
 * flat file.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
@QuarkusMain
public class Launcher implements QuarkusApplication {
    private static final Logger LOG = Logger.getLogger(Launcher.class);

    @Inject
    AgroalDataSource dataSource ;

    @ConfigProperty(name="batch.default.path", defaultValue="./tweets.csv")
    String path;

    public int run(String... args) throws Exception {

        // Output file
        Path tweets = Paths.get(args.length != 0 ? args[0] : path);

        // Build a batch job
        String[] fields = { "id", "user", "message" };
        Job job = new JobBuilder<ResultSet, String>()
                .reader(new JdbcRecordReader(dataSource, "select * from tweet"))
                .mapper(new JdbcRecordMapper<>(Tweet.class, fields))
                .marshaller(new DelimitedRecordMarshaller<>(Tweet.class, fields))
                .writer(new FileRecordWriter(tweets))
                .build();

        // Execute the job
        try (JobExecutor jobExecutor = new JobExecutor();) {
            JobReport jobReport = jobExecutor.execute(job);
            jobExecutor.shutdown();

            LOG.info(jobReport);
        }
        return 0;

    }

}
