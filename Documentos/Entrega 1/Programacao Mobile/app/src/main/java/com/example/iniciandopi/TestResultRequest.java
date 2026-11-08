package com.example.iniciandopi;

public class TestResultRequest {
    private String user_id;
    private String test_type;
    private String result_summary;

    public TestResultRequest(String user_id, String test_type, String result_summary) {
        this.user_id = user_id;
        this.test_type = test_type;
        this.result_summary = result_summary;
    }

    public String getUserId() { return user_id; }
    public String getTestType() { return test_type; }
    public String getResultSummary() { return result_summary; }
}
