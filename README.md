# Aplicatie bancara - proiect Java

Proiect minimal pentru tema de la facultate, construit astfel incat sa bifeze toate cerintele fara overengineering.

## Etapa I - definirea sistemului

### Actiuni / interogari disponibile in sistem

1. Adaugare sucursala bancara
2. Inregistrare client
3. Deschidere cont curent
4. Deschidere cont de economii
5. Emitere card de debit
6. Emitere card de credit
7. Depunere bani intr-un cont
8. Retragere bani dintr-un cont
9. Transfer intre doua conturi
10. Plata cu cardul
11. Blocare card
12. Aplicare dobanda pentru un cont de economii
13. Generare extras de cont
14. Afisare conturi ale unui client
15. Afisare conturi sortate dupa IBAN
16. Calcul sold total gestionat de banca

### Tipuri de obiecte din sistem

1. `Customer`
2. `BankBranch`
3. `BankAccount`
4. `CurrentAccount`
5. `SavingsAccount`
6. `Card`
7. `DebitCard`
8. `CreditCard`
9. `Transaction`
10. `BankStatement`

## Cerinte acoperite

- clase cu atribute private / protected si metode de acces
- colectii diferite: `HashMap`, `ArrayList`, `TreeSet`
- colectie sortata: `TreeSet<BankAccount>`
- mostenire: `BankAccount -> CurrentAccount / SavingsAccount`, `Card -> DebitCard / CreditCard`
- clasa serviciu: `BankingService`
- clasa de pornire: `Main`

## Rulare

```bash
javac -d out $(find src -name "*.java")
java -cp out banking.Main
```
