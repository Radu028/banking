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
- colectii: HashMap, ArrayList, TreeSet
- TreeSet pt sortare conturi dupa IBAN
- mostenire (BankAccount -> CurrentAccount/SavingsAccount, Card -> DebitCard/CreditCard)
- clasa serviciu (BankingService)
- clasa Main

## Rulare

```bash
javac -d out $(find src -name "*.java")
java -cp out banking.Main
```
