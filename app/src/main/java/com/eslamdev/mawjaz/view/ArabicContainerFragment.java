package com.eslamdev.mawjaz.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.eslamdev.mawjaz.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ArabicContainerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_container, container, false);

        TabLayout subTabLayout = view.findViewById(R.id.sub_tab_layout);
        ViewPager2 subViewPager = view.findViewById(R.id.sub_view_pager);

        // This is the main adapter for the "Arabic" section
        ArabicPagerAdapter adapter = new ArabicPagerAdapter(requireActivity());
        subViewPager.setAdapter(adapter);

        // Setup the main tabs: "Arabic Movies" and "Arabic TV Shows"
        new TabLayoutMediator(subTabLayout, subViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.arabic_movies);
                    break;
                case 1:
                    tab.setText(R.string.arabic_tv_shows);
                    break;
            }
        }).attach();

        return view;
    }

    // This adapter creates the fragments that will hold the sub-tabs
    private static class ArabicPagerAdapter extends FragmentStateAdapter {
        public ArabicPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    // The first tab shows the container for Arabic movies
                    return new ArabicMoviesContainerFragment();
                case 1:
                    // The second tab shows the container for Arabic TV shows
                    return new ArabicTvShowsContainerFragment();
                default:
                    return new ArabicMoviesContainerFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}