# Banking app - Java

## Etapa I

**Actiuni:**

1. Adaugare sucursala
2. Inregistrare client
3. Deschidere cont curent
4. Deschidere cont economii
5. Emitere card debit
6. Emitere card credit
7. Depunere
8. Retragere
9. Transfer intre conturi
10. Plata cu cardul
11. Blocare card
12. Aplicare dobanda (cont economii)
13. Extras de cont
14. Afisare conturi client
15. Afisare conturi sortate dupa IBAN
16. Sold total banca

**Obiecte:** Customer, BankBranch, BankAccount, CurrentAccount, SavingsAccount, Card, DebitCard, CreditCard, Transaction, BankStatement

## Ce acopera

- atribute private/protected + getteri/setteri
- colectii: ArrayList, TreeSet
- TreeSet pentru sortare conturi dupa IBAN
- mostenire (BankAccount -> CurrentAccount/SavingsAccount, Card -> DebitCard/CreditCard)
- clasa serviciu (BankingService)
- clasa Main

## Etapa II

- `schema.sql` complet cu PK si mai multe FK
- `db.properties`
- `DatabaseConnection` singleton
- interfata generica `Repository<T, ID>`
- CRUD complet pentru `BankBranch`, `Customer`, `BankAccount`, `Card`, `Transaction`
- toate SQL-urile folosesc `PreparedStatement`
- toate operatiile JDBC folosesc `try-with-resources`
- tranzactie JDBC explicita cu `commit/rollback` in `transfer`
- 3 interogari SQL cu `JOIN`
- `AuditService` CSV thread-safe

### 10 actiuni principale auditate

1. `addBranch`
2. `addCustomer`
3. `openCurrentAccount`
4. `openSavingsAccount`
5. `issueDebitCard`
6. `issueCreditCard`
7. `deposit`
8. `withdraw`
9. `transfer`
10. `payWithCard`

In plus, proiectul mai auditeaza si alte operatii utile: `blockCard`, `applyInterestToSavingsAccount`, `getCustomerAccounts`, `getAllAccountsSorted`, `generateStatement`, `getTotalBankBalance` si rapoartele cu `JOIN`.

### Ce poti arata rapid la prezentare

1. Initializarea bazei de date din `schema.sql`
2. Repository generic + implementari JDBC
3. Tranzactia explicita din metoda `transfer`
4. `audit.csv` generat automat
5. Cele 3 rapoarte SQL cu `JOIN`

## Rulare

```bash
javac -cp "lib/*" -d out $(find src -name "*.java")
java -cp "out:lib/*" banking.Main
```
