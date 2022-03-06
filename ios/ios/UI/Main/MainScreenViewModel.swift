//
//  MainScreenViewModel.swift
//  ios
//
//  Created by lizhaotailang on 2022/3/6.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import common

class MainScreenViewModel: ObservableObject {
    
    @Published var eventsResource: Resource<ArrayClass<Event>>? = nil
    @Published var notificationsResource: Resource<ArrayClass<common.Notification>>? = nil
    @Published var exploreResource: Resource<Pair<ArrayClass<TrendingDeveloper>, ArrayClass<TrendingRepository>>>? = nil
    
    private let eventsApi = EventApiWrapper(api: EventApi(ktorClient: KtorClient(requireAuth: false, accessToken: nil).httpClient))
    private var eventsPageLinks: PageLinks? = nil
    
    private let notificationsApi = NotificationApiWrapper(api: NotificationApi(ktorClient: KtorClient(requireAuth: false, accessToken: nil).httpClient))
    private var notificationsPageLinks: PageLinks? = nil
    
    private let exploreApi = TrendingApiWrapper(api: TrendingApi(ktorClient: KtorClient(requireAuth: false, accessToken: nil).httpClient))
    
    init() {
        loadEventsData(forceRefresh: true)
        loadNotificationsData(forceRefresh: true)
        loadExploreData()
    }
    
    func loadEventsData(forceRefresh: Bool) {
        if forceRefresh {
            eventsPageLinks = nil
        } else {
            if eventsResource != nil
                && eventsResource?.status == .loading {
                return
            }
        }
        
        self.eventsResource = Resource(status: .loading, data: self.eventsResource?.data, e: nil)
        
        if (eventsPageLinks?.next).isNullOrEmpty {
            eventsApi.listPublicEventThatAUserHasReceived(username: "TonnyL", page: 0, perPage: 16) { [self] resp, error in
                self.handleEventsResp(
                    forceRefresh: forceRefresh,
                    resp: resp,
                    error: error == nil ? nil : KotlinException(message: error?.localizedDescription)
                )
            }
        } else {
            eventsApi.listPublicEventThatAUserHasReceivedByUrl(url: (eventsPageLinks?.next)!) { resp, error in
                self.handleEventsResp(
                    forceRefresh: forceRefresh,
                    resp: resp,
                    error: error == nil ? nil : KotlinException(message: error?.localizedDescription)
                )
            }
        }
    }
    
    private func handleEventsResp(
        forceRefresh: Bool,
        resp: common.Result?,
        error: KotlinException?
    ) {
        if error == nil {
            if resp is common.ResultSuccess<common.Pair<NSArray, PageLinks>> {
                let success = (resp as! common.ResultSuccess<common.Pair<NSArray, PageLinks>>).value
                self.eventsPageLinks = success?.second
                let events: [Event] = success?.first?.compactMap({ $0 as? Event }) ?? []
                var existing: Array<Event>
                if forceRefresh {
                    existing = []
                } else {
                    existing = eventsResource?.data?.array ?? []
                }
                existing.append(contentsOf: events)
                
                self.eventsResource = Resource(status: .success, data: ArrayClass(array: existing), e: nil)
            } else {
                let failure = resp as! common.ResultFailure<common.Pair<NSArray, PageLinks>>
                self.eventsResource = Resource(status: .error, data: nil, e: failure.error)
            }
        } else {
            self.eventsResource = Resource(status: .error, data: nil, e: error)
        }
    }
    
    func loadNotificationsData(forceRefresh: Bool) {
        if forceRefresh {
            notificationsPageLinks = nil
        } else {
            if notificationsResource != nil
                && notificationsResource?.status == .loading {
                return
            }
        }
        
        self.notificationsResource = Resource(status: .loading, data: self.notificationsResource?.data, e: nil)
        if (notificationsPageLinks?.next).isNullOrEmpty {
            notificationsApi.listNotifications(all: true, page: 0, perPage: 16) { resp, error in
                self.handleNotificationsResp(
                    forceRefresh: forceRefresh,
                    resp: resp,
                    error: error == nil ? nil : KotlinException(message: error?.localizedDescription)
                )
            }
        } else {
            notificationsApi.listNotificationsByUrl(url: (notificationsPageLinks?.next)!) { resp, error in
                self.handleNotificationsResp(
                    forceRefresh: forceRefresh,
                    resp: resp,
                    error: error == nil ? nil : KotlinException(message: error?.localizedDescription)
                )
            }
        }
    }
    
    private func handleNotificationsResp(
        forceRefresh: Bool,
        resp: common.Result?,
        error: KotlinException?
    ) {
        if error == nil {
            if resp is common.ResultSuccess<Pair<NSArray, PageLinks>> {
                let success = (resp as! common.ResultSuccess<common.Pair<NSArray, PageLinks>>).value
                self.notificationsPageLinks = success?.second
                let notifications: [common.Notification] = success?.first?.compactMap({ $0 as? common.Notification }) ?? []
                var existing: Array<common.Notification>
                if forceRefresh {
                    existing = []
                } else {
                    existing = notificationsResource?.data?.array ?? []
                }
                existing.append(contentsOf: notifications)
                
                self.notificationsResource = Resource(status: .success, data: ArrayClass(array: existing), e: nil)
            } else {
                let failure = resp as! common.ResultFailure<common.Pair<NSArray, PageLinks>>
                self.notificationsResource = Resource(status: .error, data: nil, e: failure.error)
            }
        } else {
            self.notificationsResource = Resource(status: .error, data: nil, e: error)
        }
    }
    
    func loadExploreData() {
        if self.exploreResource != nil
            && self.exploreResource!.status == Status.loading {
            return
        }
        
        var repos: ArrayClass<TrendingRepository>? = nil
        var developers: ArrayClass<TrendingDeveloper>? = nil
        
        self.exploreResource = Resource(status: .loading, data: self.exploreResource?.data, e: nil)
        
        exploreApi.listTrendingDevelopers(language: "kotlin", spokenLanguage: "", since: "daily") { resp, error in
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
                    self.exploreResource = Resource(status: .error, data: self.exploreResource?.data, e: kotlinException)
                } else {
                    self.exploreResource = Resource(status: .success, data: Pair(first: developers!, second: repos!), e: nil)
                }
            }
        }
        
        exploreApi.listTrendingRepositories(language: "kotlin", spokenLanguage: "", since: "daily") { resp, error in
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
                    self.exploreResource = Resource(status: .error, data: self.exploreResource?.data, e: kotlinException)
                } else {
                    self.exploreResource = Resource(status: .success, data: Pair(first: developers!, second: repos!), e: nil)
                }
            }
        }
    }
    
}
