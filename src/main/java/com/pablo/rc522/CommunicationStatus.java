package com.pablo.rc522;

enum CommunicationStatus {

    MI_OK(0),
    MI_NOTAGERR(1),
    MI_ERR(2),

    ;

    private final int id;

    CommunicationStatus(final int id) {
        this.id = id;
    }

    int getId() {
        return id;
    }
}
