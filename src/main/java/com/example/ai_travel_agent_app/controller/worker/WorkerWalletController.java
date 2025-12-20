package com.example.ai_travel_agent_app.controller.worker;

import com.example.ai_travel_agent_app.dto.WalletDTO;
import com.example.ai_travel_agent_app.dto.TransactionDTO;
import com.example.ai_travel_agent_app.dto.QRCodeResponseDTO;
import com.example.ai_travel_agent_app.service.WalletService;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/worker/wallet")
public class WorkerWalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<WalletDTO> getWalletBalance() {
        String userEmail = UserFromAuth.getUserEmail();
        WalletDTO balance = walletService.getWorkerWalletBalance(userEmail);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getWalletTransactions() {
        
        String userEmail = UserFromAuth.getUserEmail();
        List<TransactionDTO> transactions = walletService.getWorkerTransactions(userEmail);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/generate-qr")
    public ResponseEntity<QRCodeResponseDTO> generateQRCode(@RequestBody Map<String, Object> request) {
        String userEmail = UserFromAuth.getUserEmail();
        Double amount = Double.valueOf(request.get("amount").toString());
        
        if (amount < 10000 || amount > 10000000) {
            return ResponseEntity.badRequest().build();
        }

        String transactionId = UUID.randomUUID().toString();

        String qrCodeUrl = walletService.generateTopUpQRCode(userEmail, transactionId, amount);
        
        QRCodeResponseDTO response = new QRCodeResponseDTO();
        response.setQrCodeUrl(qrCodeUrl);
        response.setTransactionId(transactionId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<TransactionDTO> getTransactionStatus(@PathVariable String transactionId) {
        String userEmail = UserFromAuth.getUserEmail();
        TransactionDTO transaction = walletService.getTransactionStatus(userEmail, transactionId);
        
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(transaction);
    }
}
