package com.casemgr.enumtype;

public enum EvaluateItem {
	Item1(0), Item2(1), Item3(2);
	
    private Integer value;

    private EvaluateItem(int value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
