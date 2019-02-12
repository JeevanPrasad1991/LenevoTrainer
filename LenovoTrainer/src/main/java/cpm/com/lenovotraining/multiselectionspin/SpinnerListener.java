package cpm.com.lenovotraining.multiselectionspin;

import java.util.ArrayList;

import cpm.com.lenovotraining.xmlgettersetter.SaleTeamGetterSetter;


/**
 * Created by jeevanp on 2/2/2018.
 */

public interface SpinnerListener {
    void onItemsSelected(ArrayList<SaleTeamGetterSetter> items);
}