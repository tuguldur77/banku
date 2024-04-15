package transaction;

import database.AccountDao;
import database.UserDao;

import java.io.IOException;
import java.util.Scanner;

public class TransferService {
    private final AccountDao accountDao;
    private final UserDao userDao;
    private final Scanner scanner;

    public TransferService(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
    }

    public void transfer(String loggedInUserId) {
        try {
            String senderAccountNumber = userDao.getAccountNumber(loggedInUserId);
            if (senderAccountNumber == null) {
                System.out.println("받는 사람 계좌번호 못 찾았습니다.");
                return;
            }

            System.out.println("[송금 서비스]");
            System.out.println("============================================");
            System.out.println("('q'를 입력할 시 이전 화면으로 돌아갑니다.)");
            System.out.println("송금할 계좌번호와 금액을 입력하세요");
            System.out.println("============================================");

            while (true) {
                System.out.print("받는 사람 계좌번호: ");
                String receiverAccountNumber = scanner.nextLine();

                if (receiverAccountNumber.equals("q")) {
                    System.out.println("이전 화면으로 돌아갑니다.");
                    return;
                }

                if (!isValidAccountNumber(receiverAccountNumber)) {
                    System.out.println("올바른 계좌 번호를 입력하세요!");
                    continue;
                }

                System.out.print("송금할 금액: ₩ ");
                String amountStr = scanner.nextLine();

                if (!isValidAmount(amountStr)) {
                    System.out.println("올바른 금액을 입력하세요!");
                    continue;
                }

                int amount = Integer.parseInt(amountStr.replace(",", "")); // Remove commas if present

                int senderBalance = accountDao.getBalance(senderAccountNumber);
                if (senderBalance < amount) {
                    System.out.println("잔액이 부족합니다!");
                    System.out.println("현재 잔액: ₩ " + senderBalance);
                    continue;
                }

                int receiverBalance = accountDao.getBalance(receiverAccountNumber);

                senderBalance -= amount;
                receiverBalance += amount;

                accountDao.updateBalance(senderAccountNumber, senderBalance);
                accountDao.updateBalance(receiverAccountNumber, receiverBalance);
                System.out.println();
                System.out.println("송금이 완료되었습니다.");
                break;
            }
        } catch (IOException e) {
            System.out.println("송금 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private boolean isValidAccountNumber(String accountNumber) {
        return accountNumber.matches("\\d{6}-\\d{6}");
    }

    private boolean isValidAmount(String amountStr) {
        return amountStr.matches("\\d{1,3}(,\\d{3})*(\\.\\d+)?");
    }

}