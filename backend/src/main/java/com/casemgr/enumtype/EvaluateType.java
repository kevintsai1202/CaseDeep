package com.casemgr.enumtype;

public enum EvaluateType {
	Client(0), Provider(1);
	
    private Integer value;

    private EvaluateType(int value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
