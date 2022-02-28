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
    
    private let api = TrendingApiWrapper(api: TrendingApi(ktorClient: KtorClient(requireAuth: false, accessToken: nil).httpClient))

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
            var kotlinException: KotlinException? = error == nil ? nil : KotlinException(message: error?.localizedDescription)
            if resp is common.ResultSuccess<NSArray> {
                let success = resp as! common.ResultSuccess<NSArray>
                developers = ArrayClass(
                    array: success.value?.compactMap {
                        $0 as? TrendingDeveloper
                    } ?? []
                )
            } else {
                let failure = resp as! common.ResultFailure<NSArray>
                if kotlinException == nil {
                    kotlinException = failure.error
                }
            }
            
            if repos != nil {
                if kotlinException != nil {
                    self.dataResource = Resource(status: .error, data: self.dataResource?.data, e: kotlinException)
                } else {
                    self.dataResource = Resource(status: .success, data: Pair(first: developers!, second: repos!), e: nil)
                }
            }
        }
        
        api.listTrendingRepositories(language: "kotlin", spokenLanguage: "", since: "daily") { resp, error in
            var kotlinException: KotlinException? = error == nil ? nil : KotlinException(message: error?.localizedDescription)
            if resp is common.ResultSuccess<NSArray> {
                let success = resp as! common.ResultSuccess<NSArray>
                repos = ArrayClass(
                    array: success.value?.compactMap {
                        $0 as? TrendingRepository
                    } ?? []
                )
            } else {
                let failure = resp as! common.ResultFailure<NSArray>
                if kotlinException == nil {
                    kotlinException = failure.error
                }
            }
            
            if developers != nil {
                if kotlinException != nil {
                    self.dataResource = Resource(status: .error, data: self.dataResource?.data, e: kotlinException)
                } else {
                    self.dataResource = Resource(status: .success, data: Pair(first: developers!, second: repos!), e: nil)
                }
            }
        }
    }
    
}
