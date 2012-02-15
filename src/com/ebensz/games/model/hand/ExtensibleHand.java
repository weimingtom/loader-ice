package com.ebensz.games.model.hand;

import com.ebensz.games.model.poker.Poker;

import java.util.Arrays;

/**
 * User: Mike.Hu
 * Date: 11-12-22
 * Time: 下午3:06
 */
public abstract class ExtensibleHand extends Hand {

    protected ExtensibleHand(Poker key, Hand[] extensions) {
        super(key);

        validateExtensions(extensions);

        this.extensions = extensions;
    }

    public final Hand[] getExtensions() {
        return extensions;
    }

    public void setExtensions(Hand[] extensions) {
        validateExtensions(extensions);
        this.extensions = extensions;
    }

    @Override
    public String toString() {
        if (extensions == null) return super.toString();

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (Hand extension : extensions) {
            sb.append(extension.toString());
            sb.append(";");
        }

        sb.append("}");

        return super.toString() + sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ExtensibleHand that = (ExtensibleHand) o;

        if (!Arrays.equals(extensions, that.extensions)) return false;

        return true;
    }


    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (extensions != null ? Arrays.hashCode(extensions) : 0);
        return result;
    }

    private void validateExtensions(Hand[] extensions) {
        if (extensions != null) {
            int eachSize = extensions[0].size();

            if (eachSize > 2)
                throw new IllegalArgumentException("extensions error !" + extensions);

            Class eachType = extensions[0].getClass();

            for (int i = 1; i < extensions.length; i++) {
                Hand extension = extensions[i];

                if (extension.size() != eachSize || extension.getClass() != eachType)
                    throw new IllegalArgumentException("extensions error !" + extensions);
            }

        }
    }

    protected Hand[] extensions;
}
