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
 
    var body: some View {
        TabView(selection: $selectedTab){
            TimelineScreen().tabItem {
                VStack {
                    Image(systemName: "waveform.path.ecg")
                    Text(NSLocalizedString("MainTab.Timeline", comment: ""))
                }
            }.tag(TabIdentifier.timeline)
            
            ExploreScreen().tabItem {
                VStack {
                    Image(systemName: "safari")
                    Text(NSLocalizedString("MainTab.Explore", comment: ""))
                }
            }.tag(TabIdentifier.explore)
            
            InboxScreen().tabItem {
                VStack {
                    Image(systemName: "tray")
                    Text(NSLocalizedString("MainTab.Inbox", comment: ""))
                }
            }.tag(TabIdentifier.inbox)
            
            SearchScreen().tabItem {
                VStack {
                    Image(systemName: "magnifyingglass")
                    Text(NSLocalizedString("MainTab.Search", comment: ""))
                }
            }.tag(TabIdentifier.search)
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
