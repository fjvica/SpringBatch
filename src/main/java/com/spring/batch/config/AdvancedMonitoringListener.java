package com.spring.batch.config;

import org.springframework.batch.core.*;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Listener avanzado que extiende JobExecutionListener y StepExecutionListener
 * para logging detallado y métricas personalizadas.
 */
@Component
public class AdvancedMonitoringListener implements JobExecutionListener, StepExecutionListener {

    private Map<String, Integer> errorCountByType = new HashMap<>();
    private long stepStartTime;

    // =================== JOB ===================
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("[Job] Iniciando Job: " + jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("[Job] Finalizado Job: " + jobExecution.getJobInstance().getJobName()
                + ", Estado: " + jobExecution.getStatus()
                + ", Total errores por tipo: " + errorCountByType);
        errorCountByType.clear();
    }

    // =================== STEP ===================
    @Override
    public void beforeStep(StepExecution stepExecution) {
        stepStartTime = Instant.now().toEpochMilli();
        System.out.println("[Step] Iniciando Step: " + stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long duration = Instant.now().toEpochMilli() - stepStartTime;
        System.out.println("[Step] Step finalizado: " + stepExecution.getStepName()
                + ", Estado: " + stepExecution.getStatus()
                + ", Lecturas: " + stepExecution.getReadCount()
                + ", Escritos: " + stepExecution.getWriteCount()
                + ", Duracion: " + duration + " ms"
                + ", Errores: " + errorCountByType);
        return stepExecution.getExitStatus();
    }

    // Metodo para registrar errores por tipo
    public void registerError(Exception ex) {
        String key = ex.getClass().getSimpleName();
        errorCountByType.put(key, errorCountByType.getOrDefault(key, 0) + 1);
    }
}

