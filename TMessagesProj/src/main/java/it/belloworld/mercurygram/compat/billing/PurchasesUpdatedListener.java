package it.belloworld.tellurgram.compat.billing;

import java.util.List;

/** Stub: billing is disabled in FOSS builds. */
public interface PurchasesUpdatedListener {
    void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases);
}
