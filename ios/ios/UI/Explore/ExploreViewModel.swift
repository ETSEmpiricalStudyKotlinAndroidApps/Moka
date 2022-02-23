//
//  ExploreViewModel.swift
//  ios
//
//  Created by lizhaotailang on 2021/10/31.
//  Copyright © 2021 orgName. All rights reserved.
//

import Foundation
import common
import SwiftUI

class ExploreViewModel: ObservableObject {
    @Published var dataResource: Resource<Pair<ArrayClass<TrendingDeveloper>, ArrayClass<TrendingRepository>>>? = nil
    
    private let api = TrendingApi(ktorClient: KtorClient(requireAuth: false, accessToken: nil).httpClient)

    init() {
        refresh()
    }
    
    func refresh() {
        if self.dataResource != nil
            && self.dataResource!.status == Status.loading {
            return
        }
        
        var repos: ArrayClass<TrendingRepository>? = nil
        var developers: ArrayClass<TrendingDeveloper>? = nil
        
        self.dataResource = Resource(status: .loading, data: self.dataResource?.data, e: nil)
        
        api.listTrendingDevelopers(language: "kotlin", spokenLanguage: "", since: "daily") { resp, error in
            developers = ArrayClass(array: resp ?? [])
            
            if repos != nil {
                if error != nil {
                    self.dataResource = Resource(status: .error, data: self.dataResource?.data, e: KotlinException(message: error?.localizedDescription))
                } else {
                    self.dataResource = Resource(status: .success, data: Pair(first: developers!, second: repos!), e: nil)
                }
            }
        }
        
        api.listTrendingRepositories(language: "kotlin", spokenLanguage: "", since: "daily") { resp, error in
            repos = ArrayClass(array: resp ?? [])
            
            if developers != nil {
                if error != nil {
                    self.dataResource = Resource(status: .error, data: self.dataResource?.data, e: KotlinException(message: error?.localizedDescription))
                } else {
                    self.dataResource = Resource(status: .success, data: Pair(first: developers!, second: repos!), e: nil)
                }
            }
        }
    }
    
}
