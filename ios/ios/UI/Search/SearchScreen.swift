//
//  SearchScreen.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/27.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct SearchScreen: View {
    
    @EnvironmentObject var viewModel: MainScreenViewModel
    
    var body: some View {
        NavigationView {
            ZStack {
                
            }
            .navigationTitle(NSLocalizedString("MainTab.Search", comment: ""))
            .navigationBarItems(trailing: ProfileNavigationItem())
        }
    }
    
}
