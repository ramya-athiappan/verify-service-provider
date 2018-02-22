Troubleshooting Cryptography Issues
===================================

Verify Service Provider uses cryptographic private keys to:

* Sign messages it sends to Verify
* Decrypt messages it gets back from Verify

In both cases you generate a key pair. You send the public key to Verify and
use the private key in Verify Service Provider's configuration.

If the private key used by Verify Service Provider doesn't match the public key
you gave to Verify you will see errors.

To help diagnose these issues Verify Service Provider prints checksums of the
public keys it's expecting Verify to know about when it starts up. If you know
the certificates Verify is using you can use these checksums to verify that they
match the private keys you're using.

Checking your private keys match a certificate
----------------------------------------------

When Verify Service Provider starts up it will print a table like the following:

```
====================================================================================================
| Public Key Checksums:
( see https://github.com/alphagov/tree/master/docs/troubleshooting/crytpography-issues.md )
----------------------------------------------------------------------------------------------------
| samlSigningPublicKey             - 742471087746bced37b114ae355b19dcb0fb8171c90624d60d81d408e8e109ce
| samlPrimaryEncryptionPublicKey   - b14fb4dcbcad1cb1903b9602ddd603670a748c9b96dfa76899d1c751b96a2466
| samlSecondaryEncryptionPublicKey - null
====================================================================================================
```

Assuming you've got a copy of the certificates you sent to Verify you can check
that these match the keys you're using with openssl as follows:

```
$ openssl x509 -in vsp-signing-certificate.crt -pubkey -outform DER | openssl dgst -sha256
742471087746bced37b114ae355b19dcb0fb8171c90624d60d81d408e8e109ce

$ openssl x509 -in vsp-primary-encryption-certificate.crt -pubkey -outform DER | openssl dgst -sha256
b14fb4dcbcad1cb1903b9602ddd603670a748c9b96dfa76899d1c751b96a2466
```

