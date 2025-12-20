package com.example.ai_travel_agent_app.service.impl;

import com.example.ai_travel_agent_app.dto.WalletDTO;
import com.example.ai_travel_agent_app.dto.TransactionDTO;

import com.example.ai_travel_agent_app.model.Transaction;
import com.example.ai_travel_agent_app.model.Wallet;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.repository.TransactionRepository;
import com.example.ai_travel_agent_app.repository.worker.WalletRepository;
import com.example.ai_travel_agent_app.service.CloudinaryService;
import com.example.ai_travel_agent_app.service.WalletService;
import com.example.ai_travel_agent_app.service.worker.WorkerService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private WorkerService workerService;



    @Transactional
    @Override
    public WalletDTO getWorkerWalletBalance(String userEmail) {

        Worker worker = workerService.getWorkerByEmail(userEmail);
        Wallet wallet = walletRepository.getByWorker(worker);

        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setBalance(wallet.getBalance());
        walletDTO.setLastUpdate(wallet.getUpdatedAt());

        return walletDTO;
    }

    @Override
    @Transactional
    public List<TransactionDTO> getWorkerTransactions(String userEmail) {


        Worker worker = workerService.getWorkerByEmail(userEmail);
        Wallet wallet = walletRepository.getByWorker(worker);
        List<Transaction> transactionalList = transactionRepository.findByWallet(wallet);
        
        return transactionalList.stream()
                .map(this::toTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String generateTopUpQRCode(String userEmail, String transactionId, Double amount) {
        try {
            // Create QR code content with payment information
            String qrContent = String.format(
                "HoLi Wallet Top-up\nAmount: %s VND\nTransaction ID: %s\nEmail: %s",
                String.format("%,.0f", amount),
                transactionId,
                userEmail
            );
            
            // Generate QR code image
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrCodeBytes = outputStream.toByteArray();


            String base64QrCode = "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCodeBytes);

            createPendingTransaction(userEmail, transactionId, amount);
            
            return base64QrCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public TransactionDTO getTransactionStatus(String userEmail, String transactionId) {
        return null;
    }


    private void createPendingTransaction(String userEmail, String transactionId, Double amount) {
        // In a real implementation, you would:
        // 1. Find the user by email
        // 2. Get the wallet associated with the user
        // 3. Create a new transaction with PENDING status
        
        // This is a mock implementation
        System.out.println("Created pending transaction: " + transactionId + " for " + userEmail + " with amount " + amount);
    }


    public TransactionDTO toTransactionDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}