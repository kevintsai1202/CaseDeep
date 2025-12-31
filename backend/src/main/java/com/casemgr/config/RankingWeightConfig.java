package com.casemgr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ranking.weight")
public class RankingWeightConfig {
    private double s1 = 0.25;
    private double s2 = 0.20;
    private double s3 = 0.20;
    private double s4 = 0.15;
    private double s5 = 0.10;
    private double s6 = 0.10;

    public double getS1() {
        return s1;
    }

    public void setS1(double s1) {
        this.s1 = s1;
    }

    public double getS2() {
        return s2;
    }

    public void setS2(double s2) {
        this.s2 = s2;
    }

    public double getS3() {
        return s3;
    }

    public void setS3(double s3) {
        this.s3 = s3;
    }

    public double getS4() {
        return s4;
    }

    public void setS4(double s4) {
        this.s4 = s4;
    }

    public double getS5() {
        return s5;
    }

    public void setS5(double s5) {
        this.s5 = s5;
    }

    public double getS6() {
        return s6;
    }

    public void setS6(double s6) {
        this.s6 = s6;
    }
}