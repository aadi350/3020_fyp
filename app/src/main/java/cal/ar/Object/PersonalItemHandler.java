package cal.ar.Object;

import android.content.Context;

public class PersonalItemHandler extends ObjectHandler {
    public PersonalItemHandler(Context context) {
        super(context);
    }

    public void setNeutral() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        personalItemBuilder.getNeutral().select(anchorNode, transformableNode);
    }

    public void setFits() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        personalItemBuilder.getFits().select(anchorNode,transformableNode);
    }

    public void setLarge() {
        if (anchorNode == null ||  transformableNode == null) {
            return;
        }
        personalItemBuilder.getLarge().select(anchorNode,transformableNode);
    }
}
