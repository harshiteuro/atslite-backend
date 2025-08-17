package com.harshit.tldr.pojo;

public class AnalysisRequest {
    private String resumeContent;
    private String jobDescription;
    private String fileName;
    private String model; // <-- model comes from frontend

    // Getters & Setters
    public String getResumeContent() {
        return resumeContent;
    }
    public void setResumeContent(String resumeContent) {
        this.resumeContent = resumeContent;
    }

    public String getJobDescription() {
        return jobDescription;
    }
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
}
