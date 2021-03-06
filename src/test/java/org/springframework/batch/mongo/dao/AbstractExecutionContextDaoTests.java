package org.springframework.batch.mongo.dao;

import com.mongodb.DB;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.mongo.config.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ExecutionContextDao} implementations.
 */
@SuppressWarnings({"FieldCanBeLocal"})
public abstract class AbstractExecutionContextDaoTests {

    private JobInstanceDao jobInstanceDao;

    private JobExecutionDao jobExecutionDao;

    private StepExecutionDao stepExecutionDao;

    private ExecutionContextDao contextDao;

    private JobExecution jobExecution;

    private StepExecution stepExecution;

    @Autowired
    @Database(Database.Purpose.BATCH)
    protected DB db;

    @Before
    public void setUp() {
        db.dropDatabase();
        jobInstanceDao = getJobInstanceDao();
        jobExecutionDao = getJobExecutionDao();
        stepExecutionDao = getStepExecutionDao();
        contextDao = getExecutionContextDao();

        JobInstance ji = jobInstanceDao.createJobInstance("testJob", new JobParameters());
        jobExecution = new JobExecution(ji);
        jobExecutionDao.saveJobExecution(jobExecution);
        stepExecution = new StepExecution("stepName", jobExecution);
        stepExecutionDao.saveStepExecution(stepExecution);


    }

    /**
     * @return Configured {@link ExecutionContextDao} implementation ready for
     *         use.
     */
    protected abstract JobExecutionDao getJobExecutionDao();

    /**
     * @return Configured {@link ExecutionContextDao} implementation ready for
     *         use.
     */
    protected abstract JobInstanceDao getJobInstanceDao();

    /**
     * @return Configured {@link ExecutionContextDao} implementation ready for
     *         use.
     */
    protected abstract StepExecutionDao getStepExecutionDao();

    /**
     * @return Configured {@link ExecutionContextDao} implementation ready for
     *         use.
     */
    protected abstract ExecutionContextDao getExecutionContextDao();

    @Transactional
    @Test
    public void testSaveAndFindJobContext() {

        ExecutionContext ctx = new ExecutionContext(Collections.<String, Object>singletonMap("key", "value"));
        jobExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(jobExecution);

        ExecutionContext retrieved = contextDao.getExecutionContext(jobExecution);
        assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testSaveAndFindEmptyJobContext() {

        ExecutionContext ctx = new ExecutionContext();
        jobExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(jobExecution);

        ExecutionContext retrieved = contextDao.getExecutionContext(jobExecution);
        assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testUpdateContext() {

        ExecutionContext ctx = new ExecutionContext(Collections
                .<String, Object>singletonMap("key", "value"));
        jobExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(jobExecution);

        ctx.putLong("longKey", 7);
        contextDao.updateExecutionContext(jobExecution);

        ExecutionContext retrieved = contextDao.getExecutionContext(jobExecution);
        assertEquals(ctx, retrieved);
        assertEquals(7, retrieved.getLong("longKey"));
    }

    @Transactional
    @Test
    public void testSaveAndFindStepContext() {

        ExecutionContext ctx = new ExecutionContext(Collections.<String, Object>singletonMap("key", "value"));
        stepExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(stepExecution);

        ExecutionContext retrieved = contextDao.getExecutionContext(stepExecution);
        assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testSaveAndFindEmptyStepContext() {

        ExecutionContext ctx = new ExecutionContext();
        stepExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(stepExecution);

        ExecutionContext retrieved = contextDao.getExecutionContext(stepExecution);
        assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testUpdateStepContext() {

        ExecutionContext ctx = new ExecutionContext(Collections.<String, Object>singletonMap("key", "value"));
        stepExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(stepExecution);

        ctx.putLong("longKey", 7);
        contextDao.updateExecutionContext(stepExecution);

        ExecutionContext retrieved = contextDao.getExecutionContext(stepExecution);
        assertEquals(ctx, retrieved);
        assertEquals(7, retrieved.getLong("longKey"));
    }

    @Transactional
    @Test
    public void testStoreInteger() {

        ExecutionContext ec = new ExecutionContext();
        ec.put("intValue", 343232);
        stepExecution.setExecutionContext(ec);
        contextDao.saveExecutionContext(stepExecution);
        ExecutionContext restoredEc = contextDao.getExecutionContext(stepExecution);
        assertEquals(ec, restoredEc);
    }

}
