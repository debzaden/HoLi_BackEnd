package com.example.ai_travel_agent_app.service;

import com.example.ai_travel_agent_app.dto.WalletDTO;
import com.example.ai_travel_agent_app.dto.TransactionDTO;

import java.util.List;

public interface WalletService {
    

    WalletDTO getWorkerWalletBalance(String userEmail);
    

    List<TransactionDTO> getWorkerTransactions(String userEmail);
    

    String generateTopUpQRCode(String userEmail, String transactionId, Double amount);
    

    TransactionDTO getTransactionStatus(String userEmail, String transactionId);

}