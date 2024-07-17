package com.liga.orsr.model;

import java.time.LocalDate;

public class LegalEntity {

    Long identificationNumber;
    String insertNumber;
    String court;
    String businessName;
    String registeredAddress;
    LocalDate entryDate;
    LocalDate deletionDate;
    String legalForm;
    String jsonAllOtherInfo;

    public Long getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(Long identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getInsertNumber() {
        return insertNumber;
    }

    public void setInsertNumber(String insertNumber) {
        this.insertNumber = insertNumber;
    }

    public String getCourt() {
        return court;
    }

    public void setCourt(String court) {
        this.court = court;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getRegisteredAddress() {
        return registeredAddress;
    }

    public void setRegisteredAddress(String registeredAddress) {
        this.registeredAddress = registeredAddress;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public LocalDate getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(LocalDate deletionDate) {
        this.deletionDate = deletionDate;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    public String getJsonAllOtherInfo() {
        return jsonAllOtherInfo;
    }

    public void setJsonAllOtherInfo(String jsonAllOtherInfo) {
        this.jsonAllOtherInfo = jsonAllOtherInfo;
    }
}
