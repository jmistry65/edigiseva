package com.edigiseva.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edigiseva.message.request.BankRequest;
import com.edigiseva.model.Bank;
import com.edigiseva.model.Users;
import com.edigiseva.model.Wallet;
import com.edigiseva.repository.BankRepository;
import com.edigiseva.repository.UserRepository;
import com.edigiseva.repository.WalletRepository;
import com.edigiseva.service.BankService;

@Service
public class BankServiceImpl implements BankService {

	@Autowired
	private BankRepository bankRepo;

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private WalletRepository walletRepo;

	@Override
	public Bank setUserBank(@Valid BankRequest request, Users user) {
		List<Bank> userBankList = findByUser(user);
		if (!userBankList.isEmpty()) {
			List<Bank> bankList = new ArrayList<>();
			userBankList.forEach( e -> {e.setActive(false); bankList.add(e);});
			bankRepo.saveAll(bankList);
		}
		Bank bank = new Bank();
		bank.setAccountNo(request.getAccountNo());
		bank.setBankName(request.getBankName());
		bank.setBranchname(request.getBranchName());
		bank.setIfsc(request.getIfscCode());
		bank.setUser(user);
		bank = bankRepo.save(bank);
		List<Wallet> walletList = walletRepo.findByUser(user);
		Wallet wallet = null;
		if (walletList.isEmpty()) {
			wallet = new Wallet();
			wallet.setBank(bank);
			wallet.setUser(user);
		} else {
			wallet = walletList.get(0);
			wallet.setBank(bank);
		}
		walletRepo.save(wallet);
		return bank;

	}

	@Override
	public List<Bank> findByUser(Users user) {
		Optional<Users> userData = userRepo.findById(user.getId());
		List<Bank> bankList = new ArrayList<Bank>();
		if (userData.isPresent()) {
			bankList = bankRepo.findByUser(userData.get());
		}
		return bankList;
	}

}
