package com.example.rsancryption.mode;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KycDetails {
    private String correlationId;
    private String certificate_type;
    private boolean pep_flag;
    private PanDetails pan_details;
}
