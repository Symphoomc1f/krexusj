package com.java110.things.accessControl;

import com.java110.things.service.IAssessControlProcess;
import com.java110.things.service.yld04.Yld04AssessControlProcessAdapt;

public class BaseAccessControl {

    /**
     * 访问硬件接口
     */
    private IAssessControlProcess assessControlProcessImpl;

    /**
     * 获取硬件接口对象
     * @return
     */
    protected IAssessControlProcess getAssessControlProcessImpl() {
        if (assessControlProcessImpl == null) {
            assessControlProcessImpl = new Yld04AssessControlProcessAdapt();
        }
        return assessControlProcessImpl;
    }
}
