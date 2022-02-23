//
//  RepositoryViewModel.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/24.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

class RepositoryViewModel: ObservableObject {
 
    let login: String
    let repoName: String
    
    init(login: String, repoName: String) {
        self.login = login
        self.repoName = repoName
    }
    
}
