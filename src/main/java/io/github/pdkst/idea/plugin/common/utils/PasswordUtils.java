package io.github.pdkst.idea.plugin.common.utils;


import com.caojx.idea.plugin.common.constants.Constant;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * @author pdkst.zhang
 * @since 2025/03/28 11:39
 */
@UtilityClass
public class PasswordUtils {

    /**
     * 存储密码
     *
     * @param key      key
     * @param password 密码
     */
    public void setPassword(String key, String password) {
        CredentialAttributes credentialAttributes = new CredentialAttributes(CredentialAttributesKt.generateServiceName(Constant.PLUGIN_NAME, key));
        Credentials credentials = new Credentials(credentialAttributes.getUserName(), password);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    /**
     * 获取密码
     *
     * @param key key
     * @return 密码
     */
    public String getPassword(String key) {
        CredentialAttributes credentialAttributes = new CredentialAttributes(CredentialAttributesKt.generateServiceName(Constant.PLUGIN_NAME, key));
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        return Objects.nonNull(credentials) ? credentials.getPasswordAsString() : "";
    }

    /**
     * 清楚密码
     *
     * @param key key
     */
    public void clearPassword(String key) {
        CredentialAttributes credentialAttributes = new CredentialAttributes(CredentialAttributesKt.generateServiceName(Constant.PLUGIN_NAME, key));
        PasswordSafe.getInstance().set(credentialAttributes, null);
    }
}
