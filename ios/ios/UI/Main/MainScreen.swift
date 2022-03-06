//
//  MainScreen.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/26.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

enum TabIdentifier {
    case timeline
    case explore
    case inbox
    case search
}

struct MainScreen: View {
    @State private var selectedTab: TabIdentifier = .timeline
    private var mainViewModel = MainScreenViewModel()
    
    var body: some View {
        TabView(selection: $selectedTab){
            TimelineScreen()
                .tabItem {
                    Label(NSLocalizedString("MainTab.Timeline", comment: ""), systemImage: "waveform.path.ecg")
                        .environment(\.symbolVariants, .none)
                }
                .tag(TabIdentifier.timeline)
                .environmentObject(mainViewModel)
            
            ExploreScreen()
                .tabItem {
                    Label(NSLocalizedString("MainTab.Explore", comment: ""),systemImage: "safari")
                        .environment(\.symbolVariants, .none)
                }
                .tag(TabIdentifier.explore)
                .environmentObject(mainViewModel)
            
            InboxScreen()
                .tabItem {
                    Label(NSLocalizedString("MainTab.Inbox", comment: ""), systemImage: "tray")
                        .environment(\.symbolVariants, .none)
                }
                .tag(TabIdentifier.inbox)
                .environmentObject(mainViewModel)
            
            SearchScreen()
                .tabItem {
                    Label(NSLocalizedString("MainTab.Search", comment: ""), systemImage: "magnifyingglass")
                        .environment(\.symbolVariants, .none)
                }
                .tag(TabIdentifier.search)
                .environmentObject(mainViewModel)
        }
    }
}

#if DEBUG
struct MainScreen_Previews: PreviewProvider {
    static var previews: some View {
        MainScreen()
    }
}
#endif
