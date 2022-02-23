//
//  RepositoryScreen.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/24.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct RepositoryScreen: View {
    
    var login: String
    var repoName: String
    
    @ObservedObject var viewModel: RepositoryViewModel
    
    init(login: String, repoName: String) {
        self.login = login
        self.repoName = repoName
        self.viewModel = RepositoryViewModel(login: login, repoName: repoName)
    }
    
    var body: some View {
        Text("")
    }
    
}
