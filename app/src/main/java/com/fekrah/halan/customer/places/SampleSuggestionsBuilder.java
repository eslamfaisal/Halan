package com.fekrah.halan.customer.places;

import android.content.Context;

import org.cryse.widget.persistentsearch.SearchItem;
import org.cryse.widget.persistentsearch.SearchSuggestionsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SampleSuggestionsBuilder implements SearchSuggestionsBuilder {
    private Context mContext;
    private List<SearchItem> mHistorySuggestions = new ArrayList<SearchItem>();

    public SampleSuggestionsBuilder(Context context) {
        this.mContext = context;
        createHistorys();
    }

    private void createHistorys() {
        List<String> suggestions = null;
        try {
            suggestions = (List<String>) InternalStorage.readObject(mContext, "sug");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (suggestions != null) {
            if ( suggestions.size() > 0) {
                for (String sug : suggestions) {
                    SearchItem item1 = new SearchItem(
                            sug,
                            sug,
                            SearchItem.TYPE_SEARCH_ITEM_HISTORY
                    );
                    mHistorySuggestions.add(item1);
                }
            }
        }


//        SearchItem item1 = new SearchItem(
//                "eslam faisal",
//                "eslam faisal",
//                SearchItem.TYPE_SEARCH_ITEM_HISTORY
//        );
//        mHistorySuggestions.add(item1);
//        SearchItem item2 = new SearchItem(
//                "Albert Einstein",
//                "Albert Einstein",
//                SearchItem.TYPE_SEARCH_ITEM_HISTORY
//        );
//        mHistorySuggestions.add(item2);
//        SearchItem item3 = new SearchItem(
//                "John von Neumann",
//                "John von Neumann",
//                SearchItem.TYPE_SEARCH_ITEM_HISTORY
//        );
//        mHistorySuggestions.add(item3);
//        SearchItem item4 = new SearchItem(
//                "Alan Mathison Turing",
//                "Alan Mathison Turing",
//                SearchItem.TYPE_SEARCH_ITEM_HISTORY
//        );
//        mHistorySuggestions.add(item4);
    }

    @Override
    public Collection<SearchItem> buildEmptySearchSuggestion(int maxCount) {
        List<SearchItem> items = new ArrayList<SearchItem>();
        items.addAll(mHistorySuggestions);
        return items;
    }

    @Override
    public Collection<SearchItem> buildSearchSuggestion(int maxCount, String query) {
        List<SearchItem> items = new ArrayList<SearchItem>();
//        if(query.startsWith("@")) {
        SearchItem peopleSuggestion = new SearchItem(
                query,
                query,
                SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
        );
        items.add(peopleSuggestion);
//        } else if(query.startsWith("#")) {
//            SearchItem toppicSuggestion = new SearchItem(
//                    "Search Topic: " + query.substring(1),
//                    query,
//                    SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
//            );
//            items.add(toppicSuggestion);
//        } else {
//            SearchItem peopleSuggestion = new SearchItem(
//                    "Search People: " + query,
//                    "@" + query,
//                    SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
//            );
//            items.add(peopleSuggestion);
//            SearchItem toppicSuggestion = new SearchItem(
//                    "Search Topic: " + query,
//                    "#" + query,
//                    SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
//            );
//            items.add(toppicSuggestion);
//        }
//        for(SearchItem item : mHistorySuggestions) {
//            if(item.getValue().startsWith(query)) {
//                items.add(item);
//            }
//        }
        return items;
    }
}
