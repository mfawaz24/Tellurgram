<div align="center">

<img src="./TMessagesProj/src/main/res/mipmap-hdpi/icon_foreground.png" alt="TG logo" title="TG logo" width="80"/>

# Tellurgram

An unofficial de-googled, libre Telegram client for Android, forked from [Mercurygram](https://github.com/jralo7/Mercurygram).

Tellurgram keeps all of Mercurygram's privacy and security improvements and adds its own changes:

- Improved defaults for a better experience out of the box
- Enforced HTTPS
- No more TG Premium upselling and promotions
- And more!

## Download

Get the [latest APK](https://github.com/mfawaz24/Tellurgram/releases/latest).

### Verification

The APK can be verified using [apksigner](https://developer.android.com/studio/command-line/apksigner.html#options-verify):

```
apksigner verify --print-certs -v Tellurgram-arm64-v8a.apk
```

The output should include:

```
Verifies
Verified using v2 scheme (APK Signature Scheme v2): true
Verified using v3 scheme (APK Signature Scheme v3): true
```

The certificate digests should match:

```
Signer #1 certificate SHA-256 digest: 3d3e5feecdffd9d62d99e430cd46b8ae94f089cb5b47453e5b2f064819418531
Signer #1 certificate SHA-1 digest: 2d6b79fac00238dc5bd47cc2cf13135a702f3ef4
Signer #1 certificate MD5 digest: 557bb26113bc419fa5be75d8ef5dedee
```

## License

GPL-2.0. See [LICENSE](LICENSE).
