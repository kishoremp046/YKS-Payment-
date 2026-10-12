package com.saaman.app

data class Transaction(val from: String, val to: String, val amount: Double)

/**
 * Assumes all purchases are shared equally between everyone in [People.NAMES],
 * regardless of who physically paid. Each person's "fair share" is
 * grandTotal / numberOfPeople. Balance = amountPaid - fairShare.
 * Positive balance -> that person is owed money.
 * Negative balance -> that person owes money.
 * Returns the minimum set of payments needed to settle everyone up.
 */
object Settlement {

    fun computeBalances(totalsByPerson: Map<String, Double>): Map<String, Double> {
        val people = People.NAMES
        val totals = people.associateWith { totalsByPerson[it] ?: 0.0 }
        val grandTotal = totals.values.sum()
        if (people.isEmpty()) return emptyMap()
        val fairShare = grandTotal / people.size
        return totals.mapValues { (_, paid) -> paid - fairShare }
    }

    fun computeTransactions(totalsByPerson: Map<String, Double>): List<Transaction> {
        val balances = computeBalances(totalsByPerson).toMutableMap()

        val creditors = balances.filter { it.value > 0.01 }
            .toList().sortedByDescending { it.second }.toMutableList()
        val debtors = balances.filter { it.value < -0.01 }
            .toList().sortedBy { it.second }.toMutableList()

        val result = mutableListOf<Transaction>()
        var i = 0
        var j = 0
        while (i < debtors.size && j < creditors.size) {
            val (debtor, debtBalance) = debtors[i]
            val (creditor, credBalance) = creditors[j]
            val amount = minOf(-debtBalance, credBalance)

            if (amount > 0.01) {
                result.add(Transaction(debtor, creditor, amount))
            }

            val newDebtBalance = debtBalance + amount
            val newCredBalance = credBalance - amount
            debtors[i] = debtor to newDebtBalance
            creditors[j] = creditor to newCredBalance

            if (Math.abs(newDebtBalance) < 0.01) i++
            if (Math.abs(newCredBalance) < 0.01) j++
        }
        return result
    }
}
