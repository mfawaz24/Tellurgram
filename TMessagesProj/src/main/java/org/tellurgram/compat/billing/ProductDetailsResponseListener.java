package org.tellurgram.compat.billing;

import java.util.List;

/** Stub: billing is disabled in FOSS builds. */
public interface ProductDetailsResponseListener {
    void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> productDetailsList);
}
