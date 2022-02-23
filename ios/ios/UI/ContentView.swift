import SwiftUI
import Combine
import common

extension TrendingDeveloper: Identifiable {
    
}
extension TrendingRepository: Identifiable {
    
}

struct ContentView: View {
    var body: some View {
        ExploreScreen()
    }
    
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
        ContentView()
	}
}
