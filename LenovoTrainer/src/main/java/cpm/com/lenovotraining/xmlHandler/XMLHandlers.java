package cpm.com.lenovotraining.xmlHandler;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import cpm.com.lenovotraining.xmlgettersetter.AttendanceReasonGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AuditChecklistAnswerGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.AuditChecklistGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.EmpCdIsdGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.FailureGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.IsdPerformanceGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.JCPGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.LoginGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.NonWorkingReasonGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.QuestionGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.QuizQuestionGettersetter;
import cpm.com.lenovotraining.xmlgettersetter.SaleTeamGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.StoreISDGetterSetter;
import cpm.com.lenovotraining.xmlgettersetter.TrainingTopicGetterSetter;


public class XMLHandlers {


    // FAILURE XML HANDLER
    public static FailureGetterSetter failureXMLHandler(XmlPullParser xpp,
                                                        int eventType) {
        FailureGetterSetter failureGetterSetter = new FailureGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("STATUS")) {
                        failureGetterSetter.setStatus(xpp.nextText());
                    }
                    if (xpp.getName().equals("ERRORMSG")) {
                        failureGetterSetter.setErrorMsg(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return failureGetterSetter;
    }

    // LOGIN XML HANDLER
    public static LoginGetterSetter loginXMLHandler(XmlPullParser xpp,
                                                    int eventType) {
        LoginGetterSetter lgs = new LoginGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("RESULT")) {
                        lgs.setResult(xpp.nextText());
                    }
                    if (xpp.getName().equals("APP_VERSION")) {
                        lgs.setVERSION(xpp.nextText());
                    }
                    if (xpp.getName().equals("APP_PATH")) {
                        lgs.setPATH(xpp.nextText());
                    }
                    if (xpp.getName().equals("CURRENTDATE")) {
                        lgs.setDATE(xpp.nextText());
                    }

                    if (xpp.getName().equals("RIGHTNAME")) {
                        lgs.setRIGHTNAME(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lgs;
    }

    // JCP XML HANDLER
    public static JCPGetterSetter JCPXMLHandler(XmlPullParser xpp, int eventType) {
        JCPGetterSetter jcpGetterSetter = new JCPGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        jcpGetterSetter.setTable_juorney_plan_trainer(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORE_CD")) {
                        jcpGetterSetter.setSTORE_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("EMP_CD")) {
                        jcpGetterSetter.setEMP_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("VISIT_DATE")) {
                        jcpGetterSetter.setVISIT_DATE(xpp.nextText());
                    }
                    if (xpp.getName().equals("KEYACCOUNT")) {
                        jcpGetterSetter.setKEYACCOUNT(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORENAME")) {
                        jcpGetterSetter.setSTORENAME(xpp.nextText());
                    }
                    if (xpp.getName().equals("CITY")) {
                        jcpGetterSetter.setCITY(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORETYPE")) {
                        jcpGetterSetter.setSTORETYPE(xpp.nextText());
                    }
                    if (xpp.getName().equals("TMODE_CD")) {
                        jcpGetterSetter.setTMODE_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("TRAINING_MODE")) {
                        jcpGetterSetter.setTRAINING_MODE(xpp.nextText());
                    }
                    if (xpp.getName().equals("UPLOAD_STATUS")) {
                        jcpGetterSetter.setUPLOAD_STATUS(xpp.nextText());
                    }
                    if (xpp.getName().equals("CHECKOUT_STATUS")) {
                        jcpGetterSetter.setCHECKOUT_STATUS(xpp.nextText());
                    }
                    if (xpp.getName().equals("MANAGED")) {
                        jcpGetterSetter.setMANAGED(xpp.nextText());
                    }

                    if (xpp.getName().equals("LATTITUDE")) {
                        jcpGetterSetter.setLAT(xpp.nextText());
                    }
                    if (xpp.getName().equals("LONGITUDE")) {
                        jcpGetterSetter.setLONG(xpp.nextText());
                    }
                    if (xpp.getName().equals("GEOTAG")) {
                        jcpGetterSetter.setGEOTAG(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jcpGetterSetter;
    }

    //  Training Topic XML HANDLER
    public static TrainingTopicGetterSetter TrainingTopicXMLHandler(XmlPullParser xpp, int eventType) {
        TrainingTopicGetterSetter trainingTopicGetterSetter = new TrainingTopicGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        trainingTopicGetterSetter.setTable_training_topic(xpp.nextText());
                    }

                    if (xpp.getName().equals("TOPIC_CD")) {
                        trainingTopicGetterSetter.setTOPIC_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("TOPIC")) {
                        trainingTopicGetterSetter.setTOPIC(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return trainingTopicGetterSetter;
    }

    // Store ISD XML HANDLER
    public static StoreISDGetterSetter StoreISDXMLHandler(XmlPullParser xpp, int eventType) {
        StoreISDGetterSetter storeISDGetterSetter = new StoreISDGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        storeISDGetterSetter.setTable_store_isd(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORE_CD")) {
                        storeISDGetterSetter.setSTORE_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("ISD_CD")) {
                        storeISDGetterSetter.setISD_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("ISD_NAME")) {
                        storeISDGetterSetter.setISD_NAME(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return storeISDGetterSetter;
    }

    // Quiz Question XML HANDLER
    public static QuizQuestionGettersetter QuizQuestionXMLHandler(XmlPullParser xpp, int eventType) {
        QuizQuestionGettersetter quizQuestionGettersetter = new QuizQuestionGettersetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        quizQuestionGettersetter.setTable_quiz_question(xpp.nextText());
                    }

                    if (xpp.getName().equals("TOPIC_CD")) {
                        quizQuestionGettersetter.setTOPIC_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("QUESTION_CD")) {
                        quizQuestionGettersetter.setQUESTION_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("QUESTION")) {
                        quizQuestionGettersetter.setQUESTION(xpp.nextText());
                    }
                    if (xpp.getName().equals("ANSWER_CD")) {
                        quizQuestionGettersetter.setANSWER_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("ANSWER")) {
                        quizQuestionGettersetter.setANSWER(xpp.nextText());
                    }
                    if (xpp.getName().equals("RIGHT_ANSWER")) {
                        quizQuestionGettersetter.setRIGHT_ANSWER(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return quizQuestionGettersetter;
    }

    //Audit XML HANDLER
    public static AuditChecklistGetterSetter AuditCheckListXMLHandler(XmlPullParser xpp, int eventType) {
        AuditChecklistGetterSetter auditGettersetter = new AuditChecklistGetterSetter();
        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        auditGettersetter.setTable_audit_checklist(xpp.nextText());
                    }
                    if (xpp.getName().equals("CHECKLIST_CD")) {
                        auditGettersetter.setCHECKLIST_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("CHECKLIST")) {
                        auditGettersetter.setCHECKLIST(xpp.nextText());
                    }
                    ///nmew change by jeevannnnnnnnnnn
                    if (xpp.getName().equals("CHECKLIST_TYPE")) {
                        auditGettersetter.setCHECKLIST_TYPE(xpp.nextText());
                    }
                    if (xpp.getName().equals("CHECKLIST_CATEGORY_CD")) {
                        auditGettersetter.setCHECKLIST_CATEGORY_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("CHECKLIST_CATEGORY")) {
                        auditGettersetter.setCHECKLIST_CATEGORY(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return auditGettersetter;
    }

    //Todays Question Data

    public static QuestionGetterSetter QuestionXMLHandler(XmlPullParser xpp, int eventType) {
        QuestionGetterSetter qnsGetterSetter = new QuestionGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        qnsGetterSetter.setTable_question_today(xpp.nextText());
                    }

                    if (xpp.getName().equals("QUESTION_ID")) {
                        qnsGetterSetter.setQuestion_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("QUESTION")) {
                        qnsGetterSetter.setQuestion(xpp.nextText());
                    }
                    if (xpp.getName().equals("ANSWER_ID")) {
                        qnsGetterSetter.setAnswer_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("ANSWER")) {
                        qnsGetterSetter.setAnswer(xpp.nextText());
                    }
                    if (xpp.getName().equals("RIGHT_ANSWER")) {
                        qnsGetterSetter.setRight_answer(xpp.nextText());
                    }
                    if (xpp.getName().equals("STATUS")) {
                        qnsGetterSetter.setStatus(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return qnsGetterSetter;
    }

    //Attendance daily


    public static AttendanceReasonGetterSetter AttendanceXMLHandler(XmlPullParser xpp, int eventType) {
        AttendanceReasonGetterSetter attendanceGetterSetter = new AttendanceReasonGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        attendanceGetterSetter.setTable_reason_attendance(xpp.nextText());
                    }

                    if (xpp.getName().equals("REASON_CD")) {
                        attendanceGetterSetter.setREASON_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("REASON")) {
                        attendanceGetterSetter.setREASON(xpp.nextText());
                    }
                    if (xpp.getName().equals("ENTRY_ALLOW")) {
                        attendanceGetterSetter.setENTRY_ALLOW(xpp.nextText());
                    }
                    if (xpp.getName().equals("STATUS")) {
                        attendanceGetterSetter.setSTATUS(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return attendanceGetterSetter;
    }

    public static NonWorkingReasonGetterSetter nonWorkinReasonXML(XmlPullParser xpp,
                                                                  int eventType) {
        NonWorkingReasonGetterSetter nonworking = new NonWorkingReasonGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        nonworking.setNonworking_table(xpp.nextText());
                    }
                    if (xpp.getName().equals("REASON_CD")) {
                        nonworking.setReason_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("REASON")) {
                        nonworking.setReason(xpp.nextText());
                    }
                    if (xpp.getName().equals("ENTRY_ALLOW")) {
                        nonworking.setEntry_allow(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nonworking;
    }

    public static IsdPerformanceGetterSetter isdPerformanceXML(XmlPullParser xpp,
                                                               int eventType) {
        IsdPerformanceGetterSetter nonworking = new IsdPerformanceGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        nonworking.setTable_isd_performance(xpp.nextText());
                    }
                    if (xpp.getName().equals("TRAINING_DATE")) {
                        nonworking.setTRAINING_DATE(xpp.nextText());
                    }
                    if (xpp.getName().equals("TRAINING_TYPE")) {
                        nonworking.setTRAINING_TYPE(xpp.nextText());
                    }
                    if (xpp.getName().equals("TOPIC")) {
                        nonworking.setTOPIC(xpp.nextText());
                    }
                    if (xpp.getName().equals("ISD_CD")) {
                        nonworking.setISD_CD(xpp.nextText());
                    }
                    if (xpp.getName().equals("ISD")) {
                        nonworking.setISD(xpp.nextText());
                    }
                    if (xpp.getName().equals("GROOMING_SCORE")) {
                        nonworking.setGROOMING_SCORE(xpp.nextText());
                    }
                    if (xpp.getName().equals("QUIZ_SCORE")) {
                        nonworking.setQUIZ_SCORE(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return nonworking;
    }

    // LOGIN XML HANDLER
    public static EmpCdIsdGetterSetter empcdXMLHandler(XmlPullParser xpp,
                                                       int eventType) {
        EmpCdIsdGetterSetter lgs = new EmpCdIsdGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("ISD_CD")) {
                        lgs.setIsd_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("EMP_ID")) {
                        lgs.setEmp_cd(xpp.nextText());
                    }

                    if (xpp.getName().equals("ISD")) {
                        lgs.setIsd(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lgs;
    }

    // LOGIN XML HANDLER
    public static AuditChecklistAnswerGetterSetter AuditCheckLANSXMLHandler(XmlPullParser xpp,
                                                                            int eventType) {
        AuditChecklistAnswerGetterSetter lgs = new AuditChecklistAnswerGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        lgs.setAuditchecklistANSTable(xpp.nextText());
                    }

                    if (xpp.getName().equals("ANSWER_CD")) {
                        lgs.setAnswer_cd(xpp.nextText());
                    }
                    if (xpp.getName().equals("ANSWER")) {
                        lgs.setAnswer(xpp.nextText());
                    }

                    if (xpp.getName().equals("CHECKLIST_CD")) {
                        lgs.setChecklist_cd(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lgs;
    }

    public static SaleTeamGetterSetter SaleTeamXMLHandler(XmlPullParser xpp,
                                                                int eventType) {
        SaleTeamGetterSetter lgs = new SaleTeamGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("META_DATA")) {
                        lgs.setSaleTEAMTable(xpp.nextText());
                    }

                    if (xpp.getName().equals("SALES_TEAM")) {
                        lgs.setTrainee(xpp.nextText());
                    }
                    if (xpp.getName().equals("SALES_TEAM_ID")) {
                        lgs.setTrainee_cd(xpp.nextText());
                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lgs;
    }

}
