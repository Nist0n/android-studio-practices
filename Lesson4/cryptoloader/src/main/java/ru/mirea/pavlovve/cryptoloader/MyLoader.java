package ru.mirea.pavlovve.cryptoloader;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {

    private byte[] encrypted;
    private byte[] keyBytes;

    public MyLoader(@NonNull Context context, Bundle args) {
        super(context);

        if (args != null) {
            encrypted = args.getByteArray("encrypted");
            keyBytes = args.getByteArray("key");
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad(); // запускаем фоновую работу
    }

    @Override
    public String loadInBackground() {

        // Выполняется в фоновом потоке

        // Восстанавливаем ключ из байтов
        SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");

        // Дешифруем сообщение
        String decrypted = CryptoUtils.decryptMsg(encrypted, key);

        // Возвращаем результат в UI поток
        return "Расшифрованный текст: " + decrypted;
    }
}