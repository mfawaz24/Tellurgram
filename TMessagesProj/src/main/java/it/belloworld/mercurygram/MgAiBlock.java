package it.belloworld.tellurgram;

import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.RequestDelegateTimestamp;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_aicompose;

/**
 * Drops requests to Telegram's AI services (compose, rich compose, summarize
 * and the AI compose tones) while {@link MgAccountConfig#blockAiServers} is on.
 * The caller gets an error back instead of a silent drop so nothing waits on a
 * response that never comes.
 */
public final class MgAiBlock {

    public static final String ERROR_TEXT = "MG_AI_SERVERS_BLOCKED";

    private MgAiBlock() {
    }

    public static boolean isAiRequest(TLObject request) {
        return request instanceof TLRPC.TL_messages_composeMessageWithAI
                || request instanceof TLRPC.TL_messages_composeRichMessageWithAI
                || request instanceof TLRPC.TL_messages_summarizeText
                || request instanceof TL_aicompose.createTone
                || request instanceof TL_aicompose.updateTone
                || request instanceof TL_aicompose.saveTone
                || request instanceof TL_aicompose.deleteTone
                || request instanceof TL_aicompose.getTone
                || request instanceof TL_aicompose.getTones
                || request instanceof TL_aicompose.getToneExample;
    }

    public static boolean dropIfBlocked(int account, TLObject request, RequestDelegate callback, RequestDelegateTimestamp timestampCallback) {
        if (!isAiRequest(request) || !UserConfig.getInstance(account).mg.blockAiServers) {
            return false;
        }
        TLRPC.TL_error error = new TLRPC.TL_error();
        error.code = 403;
        error.text = ERROR_TEXT;
        if (callback != null) {
            callback.run(null, error);
        }
        if (timestampCallback != null) {
            timestampCallback.run(null, error, 0);
        }
        return true;
    }
}
