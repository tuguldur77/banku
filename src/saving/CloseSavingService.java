package saving;

import database.AccountDao;
import database.DatabaseManager;
import database.UserDao;

import java.util.Scanner;

public class CloseSavingService {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AccountDao accountDao;
    // 추가
    private SavingProduct product1;
    private SavingProduct product2;
    private SavingProduct product3;

    public CloseSavingService(UserDao userDao, AccountDao accountDao, SavingProduct product1, SavingProduct product2, SavingProduct product3) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        this.product1 = product1;
        this.product2 = product2;
        this.product3 = product3;
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
    }

    public void doCloseService(String loggedInUserId){
        try {
            System.out.println();
            System.out.println("[예ㆍ적금 해지 서비스]");
            System.out.println("============================================");
            System.out.println("'q'를 입력할 시 이전 화면으로 돌아갑니다.");
            System.out.println("예ㆍ적금 해지 서비스를 시작합니다. 개인정보를 입력해주세요");
            System.out.println("============================================");

            boolean flag = true;
            while (flag) {
                System.out.print("이름을 입력하세요 : ");
                String inputName = scanner.nextLine();

                //추가
                String actualName = userDao.findUserNameById(loggedInUserId);
                if (!inputName.equals(actualName)) {
                    System.out.println("존재하지 않는 이름입니다.");
                    return;
                }

                System.out.print("주민등록번호를 입력하세요: ");
                String inputRRN = scanner.nextLine();
                if (!inputRRN.equals(userDao.findUserToRRN(loggedInUserId))) {
                    System.out.println("존재하지 않는 주민등록번호입니다.");
                    return;
                }

                System.out.println();
                System.out.println("[예ㆍ적금 해지 서비스]");
                System.out.println("============================================");
                System.out.println("예ㆍ적금 조회 결과:");

                String account = userDao.findUserToAccount(loggedInUserId);
                boolean result1 = accountDao.hasSavings(account, 1);
                boolean result2 = accountDao.hasSavings(account, 2);
                boolean result3 = accountDao.hasSavings(account, 3);
                boolean result4 = accountDao.hasSavings(account, 4);

                if (result1){
                    System.out.println("[1] 정기 예금");
                }
                if (result2){
                    System.out.println("[2] 6개월 적금");
                }
                if (result3){
                    System.out.println("[3] 12개월 적금");
                }
                if (result4){
                    System.out.println("[4] 24개월 저금");
                }

                System.out.println("[0] 뒤로가기");
                System.out.println("============================================");
                System.out.print("해지하실 예ㆍ적금 번호를 입력하세요 :");
                //추가
                int inputNum = scanner.nextInt();
                scanner.nextLine();
                String accountNumber = userDao.findUserToAccount(loggedInUserId);
                String amountstr = accountDao.getSavingsAmount(accountNumber, inputNum - 1);
                int amount = Integer.parseInt(amountstr);

                switch (inputNum){
                    case 1:
                        if (!result1){
                            System.out.println("올바르지 않은 메뉴입니다.");
                        }
                        flag = false;
                        break;
                    case 2:
                        if (!result2){
                            System.out.println("올바르지 않은 메뉴입니다.");
                        }
                        product1.adjustInterestRateBasedOnAmount(amount);
                        int currentMonths = product1.getCurrentMonths();
                        System.out.println(currentMonths);
                        System.out.println("1번 상품 해지 결과");
                        System.out.println("원금 : " + amount);
                        System.out.println("이자 : " + product1.calculateTotalInterest(amount, currentMonths));
                        System.out.println("합게 : " + product1.calculateTotalAmount(amount, currentMonths));
                        flag = false;
                        break;
                    case 3:
                        if (!result3){
                            System.out.println("올바르지 않은 메뉴입니다.");
                        }
                        product2.adjustInterestRateBasedOnAmount(amount);
                        currentMonths = product2.getCurrentMonths();
                        System.out.println("2번 상품 해지 결과");
                        System.out.println("원금 : " + amount);
                        System.out.println("이자 : " + product2.calculateTotalInterest(amount, currentMonths));
                        System.out.println("합게 : " + product2.calculateTotalAmount(amount, currentMonths));
                        flag = false;
                        break;
                    case 4:
                        if (!result4){
                            System.out.println("올바르지 않은 메뉴입니다.");
                        }
                        product3.adjustInterestRateBasedOnAmount(amount);
                        currentMonths = product3.getCurrentMonths();
                        System.out.println("3번 상품 해지 결과");
                        System.out.println("원금 : " + amount);
                        System.out.println("이자 : " + product3.calculateTotalInterest(amount, currentMonths));
                        System.out.println("합게 : " + product3.calculateTotalAmount(amount, currentMonths));
                        flag = false;
                        break;
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
    }
}
