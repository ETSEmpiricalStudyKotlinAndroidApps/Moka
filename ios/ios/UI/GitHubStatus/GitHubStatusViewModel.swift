//
//  GitHubStatusViewModel.swift
//  ios
//
//  Created by lizhaotailang on 2022/3/5.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import common

class GitHubStatusViewModel: ObservableObject {
    
    @Published var statusResource: Resource<GitHubStatus>? = nil
    
    private let api = GitHubStatusApiWrapper(api: GitHubStatusApi(ktorClient: KtorClient(requireAuth: false, accessToken: nil).httpClient))
    
    init() {
        loadData(forceRefresh: true)
    }
    
    func loadData(forceRefresh: Bool) {
        if statusResource?.data?.status == Status.loading {
            return
        }
        
        self.statusResource = Resource(status: .loading, data: statusResource?.data, e: nil)
        
        api.getSummary() { resp, error in
            if error != nil {
                DispatchQueue.main.async {
                    self.statusResource = Resource(status: .error, data: self.statusResource?.data, e: error?.kotlinException)
                }
            } else {
                if resp is common.ResultSuccess<GitHubStatus> {
                    var success = (resp as! common.ResultSuccess<GitHubStatus>).value
                    if success != nil {
                        success = success!.doCopy(
                            page: success!.page,
                            components: success!.components.filter {
                                !$0.name.starts(with: "Visit")
                            },
                            status: success!.status,
                            incidents: success!.incidents
                        )
                    }
                    
                    DispatchQueue.main.async {
                        self.statusResource = Resource(status: .success, data: success, e: nil)
                    }
                } else {
                    let failure = (resp as! common.ResultFailure<GitHubStatus>).error
                    
                    DispatchQueue.main.async {
                        self.statusResource = Resource(status: .error, data: nil, e: failure)
                    }
                }
            }
        }
    }
    
}
