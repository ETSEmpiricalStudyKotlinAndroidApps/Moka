package io.github.tonnyl.moka.ui.repository

import android.net.Uri
import android.text.format.DateUtils
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRowForIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import com.google.android.material.chip.Chip
import dev.chrisbanes.accompanist.coil.CoilImage
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.type.RepositoryPermission
import io.github.tonnyl.moka.type.SubscriptionState
import io.github.tonnyl.moka.widget.*
import kotlinx.datetime.Instant

@Composable
fun RepositoryScreen(scrollState: ScrollState) {
    val viewModel = viewModel<RepositoryViewModel>()
    val usersRepositoryResource by viewModel.usersRepository.observeAsState()
    val organizationsRepositoryResource by viewModel.organizationsRepository.observeAsState()
    val readmeResource by viewModel.readmeHtml.observeAsState()
    val isFollowing by viewModel.followState.observeAsState()

    when {
        (usersRepositoryResource == null && organizationsRepositoryResource == null)
                || usersRepositoryResource?.status == Status.LOADING
                || organizationsRepositoryResource?.status == Status.LOADING -> {
            LoadingScreen()
        }
        usersRepositoryResource?.data != null
                || organizationsRepositoryResource?.data != null -> {
            RepositoryScreenContent(
                scrollState = scrollState,
                usersRepository = usersRepositoryResource?.data,
                organizationsRepository = organizationsRepositoryResource?.data,
                isFollowing = isFollowing?.data == true,

                onWatchersClicked = viewModel::viewWatchers,
                onStargazersClicked = viewModel::viewStargazers,
                onForksClicked = viewModel::viewForks,
                onPrsClicked = viewModel::viewPullRequests,
                onIssuesClicked = viewModel::viewIssues,
                onProjectsClicked = viewModel::viewProjects,
                readmeResource = readmeResource
            )
        }
        else -> {
            EmptyScreenContent(
                icon = R.drawable.ic_person_outline_24,
                title = if (usersRepositoryResource?.status == Status.ERROR) {
                    R.string.user_profile_content_empty_title
                } else {
                    R.string.common_error_requesting_data
                },
                retry = R.string.common_retry,
                action = R.string.user_profile_content_empty_action
            )
        }
    }
}

@Composable
private fun RepositoryScreenContent(
    scrollState: ScrollState,
    usersRepository: Repository?,
    organizationsRepository: Repository?,
    isFollowing: Boolean,

    onWatchersClicked: () -> Unit,
    onStargazersClicked: () -> Unit,
    onForksClicked: () -> Unit,
    onIssuesClicked: () -> Unit,
    onPrsClicked: () -> Unit,
    onProjectsClicked: () -> Unit,

    readmeResource: Resource<String>?
) {
    val padding = dimensionResource(id = R.dimen.fragment_content_padding)
    ScrollableColumn(scrollState = scrollState) {
        Row(modifier = Modifier.padding(padding)) {
            CoilImage(
                request = createAvatarLoadRequest(
                    url = usersRepository?.owner?.avatarUrl
                        ?: organizationsRepository?.owner?.avatarUrl
                ),
                modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.regular_icon_size))
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.preferredWidth(padding))
            Column(modifier = Modifier.weight(1f)) {
                val ownerName = usersRepository?.ownerName ?: organizationsRepository?.ownerName
                val ownerLogin =
                    usersRepository?.owner?.login ?: organizationsRepository?.owner?.login
                if (!ownerName.isNullOrEmpty()) {
                    Text(
                        text = ownerName,
                        style = MaterialTheme.typography.body1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (!ownerLogin.isNullOrEmpty()) {
                    Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = ownerLogin,
                            style = MaterialTheme.typography.body2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (usersRepository?.viewerCanFollow == true) {
                Spacer(modifier = Modifier.preferredWidth(padding))
                OutlinedButton(onClick = {}) {
                    Text(
                        text = stringResource(
                            id = if (isFollowing) {
                                R.string.user_profile_unfollow
                            } else {
                                R.string.user_profile_follow
                            }
                        )
                    )
                }
            }
        }
        val repoName = usersRepository?.name ?: organizationsRepository?.name
        if (!repoName.isNullOrEmpty()) {
            Text(
                text = repoName,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(horizontal = padding)
            )
        }
        val desc = usersRepository?.description
            ?: organizationsRepository?.description
            ?: stringResource(id = R.string.no_description_provided)
        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
            Text(
                text = desc,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(padding)
            )
        }
        Row {
            NumberCategoryText(
                number = usersRepository?.watchersCount
                    ?: organizationsRepository?.watchersCount
                    ?: 0,
                category = stringResource(id = R.string.repository_watchers),
                onClick = onWatchersClicked,
                modifier = Modifier.weight(1f)
            )
            NumberCategoryText(
                number = usersRepository?.stargazersCount
                    ?: organizationsRepository?.stargazersCount
                    ?: 0,
                category = stringResource(id = R.string.repository_stargazers),
                onClick = onStargazersClicked,
                modifier = Modifier.weight(1f)
            )
            NumberCategoryText(
                number = usersRepository?.forksCount
                    ?: organizationsRepository?.forksCount
                    ?: 0,
                category = stringResource(id = R.string.repository_forks),
                onClick = onForksClicked,
                modifier = Modifier.weight(1f)
            )
        }
        Row {
            NumberCategoryText(
                number = usersRepository?.issuesCount
                    ?: organizationsRepository?.issuesCount
                    ?: 0,
                category = stringResource(id = R.string.repository_issues),
                onClick = onIssuesClicked,
                modifier = Modifier.weight(1f)
            )
            NumberCategoryText(
                number = usersRepository?.pullRequestsCount
                    ?: organizationsRepository?.pullRequestsCount
                    ?: 0,
                category = stringResource(id = R.string.repository_pull_requests),
                onClick = onPrsClicked,
                modifier = Modifier.weight(1f)
            )
            NumberCategoryText(
                number = usersRepository?.projectsCount
                    ?: organizationsRepository?.projectsCount
                    ?: 0,
                category = stringResource(id = R.string.repository_projects),
                onClick = onProjectsClicked,
                modifier = Modifier.weight(1f)
            )
        }
        CategoryText(textRes = R.string.repository_basic_info)
        InfoListItem(
            leadingRes = R.string.repository_branches,
            trailing = usersRepository?.defaultBranchRef?.name
                ?: organizationsRepository?.defaultBranchRef?.name ?: "",
            modifier = Modifier.clickable(onClick = {})
        )
        InfoListItem(
            leadingRes = R.string.repository_releases,
            trailing = (usersRepository?.releasesCount
                ?: organizationsRepository?.releasesCount
                ?: 0).toString(),
            modifier = Modifier.clickable(onClick = {})
        )
        InfoListItem(
            leadingRes = R.string.repository_language,
            trailing = usersRepository?.primaryLanguage?.name
                ?: organizationsRepository?.primaryLanguage?.name
                ?: stringResource(id = R.string.programming_language_unknown),
            modifier = Modifier.clickable(onClick = {})
        )
        InfoListItem(
            leadingRes = R.string.repository_license,
            trailing = usersRepository?.licenseInfo?.name
                ?: organizationsRepository?.licenseInfo?.name
                ?: "",
            modifier = Modifier.clickable(onClick = {})
        )
        val topics = usersRepository?.topics ?: organizationsRepository?.topics
        if (!topics.isNullOrEmpty()) {
            Row {
                CategoryText(textRes = R.string.repository_topics)
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(text = stringResource(id = R.string.see_all))
                }
                Spacer(modifier = Modifier.preferredWidth(padding))
            }
            LazyRowForIndexed(items = topics) { index, topic ->
                if (topic != null) {
                    Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding)))
                    AndroidView(viewBlock = { Chip(it) }) {
                        it.text = topic.topic.name
                    }
                    if (index == topics.size - 1) {
                        Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding)))
                    }
                }
            }
        }

        CategoryText(textRes = R.string.repository_readme)
        when (readmeResource?.status) {
            Status.SUCCESS -> {
                val readme = readmeResource.data
                if (readme.isNullOrEmpty()) {
                    EmptyReadmeText()
                } else {
                    AndroidView(
                        viewBlock = { ThemedWebView(it) },
                        modifier = Modifier.padding(horizontal = padding)
                    ) {
                        it.loadData(readme)
                    }
                }
            }
            Status.LOADING -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    alignment = Alignment.Center
                ) {
                    LottieLoadingComponent(modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.regular_icon_size_plus_double_padding)))
                }
            }
            // Status.ERROR, null
            else -> {
                EmptyReadmeText()
            }
        }
        val createdAt = usersRepository?.createdAt ?: organizationsRepository?.createdAt
        val updatedAt = usersRepository?.updatedAt ?: organizationsRepository?.updatedAt
        CategoryText(textRes = R.string.repository_more_info)
        if (createdAt != null) {
            InfoListItem(
                leadingRes = R.string.repository_created_at,
                trailing = DateUtils.getRelativeTimeSpanString(createdAt.toEpochMilliseconds())
                    .toString()
            )
        }
        if (updatedAt != null) {
            InfoListItem(
                leadingRes = R.string.repository_updated_at,
                trailing = DateUtils.getRelativeTimeSpanString(updatedAt.toEpochMilliseconds())
                    .toString()
            )
        }
        Spacer(modifier = Modifier.preferredHeight(padding))
        Spacer(modifier = Modifier.preferredHeight(56.dp)) // see AppBarHeight
    }
}

@Composable
private fun EmptyReadmeText() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        alignment = Alignment.Center
    ) {
        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(id = R.string.no_description_provided),
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Preview(showBackground = true, name = "RepositoryScreenContentPreview")
@Composable
private fun RepositoryScreenContentPreview() {
    RepositoryScreenContent(
        scrollState = rememberScrollState(),
        usersRepository = Repository(
            codeOfConduct = null,
            createdAt = Instant.fromEpochMilliseconds(1458315345000L),
            defaultBranchRef = Ref(
                id = "MDM6UmVmNTQyMTIzNTU6cmVmcy9oZWFkcy9tYXN0ZXI=",
                name = "master",
                prefix = "refs/heads/",
                target = GitObject(
                    abbreviatedOid = "deabc06",
                    commitResourcePath = Uri.parse("/TonnyL/PaperPlane/commit/deabc062ec138e29f8b34bcea164c8ef49881175"),
                    commitUrl = Uri.parse("https://github.com/TonnyL/PaperPlane/commit/deabc062ec138e29f8b34bcea164c8ef49881175"),
                    id = "MDY6Q29tbWl0NTQyMTIzNTU6ZGVhYmMwNjJlYzEzOGUyOWY4YjM0YmNlYTE2NGM4ZWY0OTg4MTE3NQ==",
                    oid = "deabc062ec138e29f8b34bcea164c8ef49881175"
                )
            ),
            description = "📚 PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. ",
            descriptionHTML = "<div>\n<g-emoji class=\"g-emoji\" alias=\"books\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f4da.png\">📚</g-emoji> PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. </div>",
            diskUsage = 17162,
            forkCount = 292,
            hasIssuesEnabled = true,
            hasWikiEnabled = true,
            homepageUrl = Uri.parse(""),
            id = "MDEwOlJlcG9zaXRvcnk1NDIxMjM1NQ==",
            isArchived = false,
            isFork = false,
            isLocked = false,
            isMirror = false,
            isPrivate = false,
            isTemplate = false,
            licenseInfo = License(
                body = "                                 Apache License\n                           Version 2.0, January 2004\n                        http://www.apache.org/licenses/\n\n   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n\n   1. Definitions.\n\n      \"License\" shall mean the terms and conditions for use, reproduction,\n      and distribution as defined by Sections 1 through 9 of this document.\n\n      \"Licensor\" shall mean the copyright owner or entity authorized by\n      the copyright owner that is granting the License.\n\n      \"Legal Entity\" shall mean the union of the acting entity and all\n      other entities that control, are controlled by, or are under common\n      control with that entity. For the purposes of this definition,\n      \"control\" means (i) the power, direct or indirect, to cause the\n      direction or management of such entity, whether by contract or\n      otherwise, or (ii) ownership of fifty percent (50%) or more of the\n      outstanding shares, or (iii) beneficial ownership of such entity.\n\n      \"You\" (or \"Your\") shall mean an individual or Legal Entity\n      exercising permissions granted by this License.\n\n      \"Source\" form shall mean the preferred form for making modifications,\n      including but not limited to software source code, documentation\n      source, and configuration files.\n\n      \"Object\" form shall mean any form resulting from mechanical\n      transformation or translation of a Source form, including but\n      not limited to compiled object code, generated documentation,\n      and conversions to other media types.\n\n      \"Work\" shall mean the work of authorship, whether in Source or\n      Object form, made available under the License, as indicated by a\n      copyright notice that is included in or attached to the work\n      (an example is provided in the Appendix below).\n\n      \"Derivative Works\" shall mean any work, whether in Source or Object\n      form, that is based on (or derived from) the Work and for which the\n      editorial revisions, annotations, elaborations, or other modifications\n      represent, as a whole, an original work of authorship. For the purposes\n      of this License, Derivative Works shall not include works that remain\n      separable from, or merely link (or bind by name) to the interfaces of,\n      the Work and Derivative Works thereof.\n\n      \"Contribution\" shall mean any work of authorship, including\n      the original version of the Work and any modifications or additions\n      to that Work or Derivative Works thereof, that is intentionally\n      submitted to Licensor for inclusion in the Work by the copyright owner\n      or by an individual or Legal Entity authorized to submit on behalf of\n      the copyright owner. For the purposes of this definition, \"submitted\"\n      means any form of electronic, verbal, or written communication sent\n      to the Licensor or its representatives, including but not limited to\n      communication on electronic mailing lists, source code control systems,\n      and issue tracking systems that are managed by, or on behalf of, the\n      Licensor for the purpose of discussing and improving the Work, but\n      excluding communication that is conspicuously marked or otherwise\n      designated in writing by the copyright owner as \"Not a Contribution.\"\n\n      \"Contributor\" shall mean Licensor and any individual or Legal Entity\n      on behalf of whom a Contribution has been received by Licensor and\n      subsequently incorporated within the Work.\n\n   2. Grant of Copyright License. Subject to the terms and conditions of\n      this License, each Contributor hereby grants to You a perpetual,\n      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n      copyright license to reproduce, prepare Derivative Works of,\n      publicly display, publicly perform, sublicense, and distribute the\n      Work and such Derivative Works in Source or Object form.\n\n   3. Grant of Patent License. Subject to the terms and conditions of\n      this License, each Contributor hereby grants to You a perpetual,\n      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n      (except as stated in this section) patent license to make, have made,\n      use, offer to sell, sell, import, and otherwise transfer the Work,\n      where such license applies only to those patent claims licensable\n      by such Contributor that are necessarily infringed by their\n      Contribution(s) alone or by combination of their Contribution(s)\n      with the Work to which such Contribution(s) was submitted. If You\n      institute patent litigation against any entity (including a\n      cross-claim or counterclaim in a lawsuit) alleging that the Work\n      or a Contribution incorporated within the Work constitutes direct\n      or contributory patent infringement, then any patent licenses\n      granted to You under this License for that Work shall terminate\n      as of the date such litigation is filed.\n\n   4. Redistribution. You may reproduce and distribute copies of the\n      Work or Derivative Works thereof in any medium, with or without\n      modifications, and in Source or Object form, provided that You\n      meet the following conditions:\n\n      (a) You must give any other recipients of the Work or\n          Derivative Works a copy of this License; and\n\n      (b) You must cause any modified files to carry prominent notices\n          stating that You changed the files; and\n\n      (c) You must retain, in the Source form of any Derivative Works\n          that You distribute, all copyright, patent, trademark, and\n          attribution notices from the Source form of the Work,\n          excluding those notices that do not pertain to any part of\n          the Derivative Works; and\n\n      (d) If the Work includes a \"NOTICE\" text file as part of its\n          distribution, then any Derivative Works that You distribute must\n          include a readable copy of the attribution notices contained\n          within such NOTICE file, excluding those notices that do not\n          pertain to any part of the Derivative Works, in at least one\n          of the following places: within a NOTICE text file distributed\n          as part of the Derivative Works; within the Source form or\n          documentation, if provided along with the Derivative Works; or,\n          within a display generated by the Derivative Works, if and\n          wherever such third-party notices normally appear. The contents\n          of the NOTICE file are for informational purposes only and\n          do not modify the License. You may add Your own attribution\n          notices within Derivative Works that You distribute, alongside\n          or as an addendum to the NOTICE text from the Work, provided\n          that such additional attribution notices cannot be construed\n          as modifying the License.\n\n      You may add Your own copyright statement to Your modifications and\n      may provide additional or different license terms and conditions\n      for use, reproduction, or distribution of Your modifications, or\n      for any such Derivative Works as a whole, provided Your use,\n      reproduction, and distribution of the Work otherwise complies with\n      the conditions stated in this License.\n\n   5. Submission of Contributions. Unless You explicitly state otherwise,\n      any Contribution intentionally submitted for inclusion in the Work\n      by You to the Licensor shall be under the terms and conditions of\n      this License, without any additional terms or conditions.\n      Notwithstanding the above, nothing herein shall supersede or modify\n      the terms of any separate license agreement you may have executed\n      with Licensor regarding such Contributions.\n\n   6. Trademarks. This License does not grant permission to use the trade\n      names, trademarks, service marks, or product names of the Licensor,\n      except as required for reasonable and customary use in describing the\n      origin of the Work and reproducing the content of the NOTICE file.\n\n   7. Disclaimer of Warranty. Unless required by applicable law or\n      agreed to in writing, Licensor provides the Work (and each\n      Contributor provides its Contributions) on an \"AS IS\" BASIS,\n      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n      implied, including, without limitation, any warranties or conditions\n      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n      PARTICULAR PURPOSE. You are solely responsible for determining the\n      appropriateness of using or redistributing the Work and assume any\n      risks associated with Your exercise of permissions under this License.\n\n   8. Limitation of Liability. In no event and under no legal theory,\n      whether in tort (including negligence), contract, or otherwise,\n      unless required by applicable law (such as deliberate and grossly\n      negligent acts) or agreed to in writing, shall any Contributor be\n      liable to You for damages, including any direct, indirect, special,\n      incidental, or consequential damages of any character arising as a\n      result of this License or out of the use or inability to use the\n      Work (including but not limited to damages for loss of goodwill,\n      work stoppage, computer failure or malfunction, or any and all\n      other commercial damages or losses), even if such Contributor\n      has been advised of the possibility of such damages.\n\n   9. Accepting Warranty or Additional Liability. While redistributing\n      the Work or Derivative Works thereof, You may choose to offer,\n      and charge a fee for, acceptance of support, warranty, indemnity,\n      or other liability obligations and/or rights consistent with this\n      License. However, in accepting such obligations, You may act only\n      on Your own behalf and on Your sole responsibility, not on behalf\n      of any other Contributor, and only if You agree to indemnify,\n      defend, and hold each Contributor harmless for any liability\n      incurred by, or claims asserted against, such Contributor by reason\n      of your accepting any such warranty or additional liability.\n\n   END OF TERMS AND CONDITIONS\n\nAPPENDIX: How to apply the Apache License to your work.\n\n      To apply the Apache License to your work, attach the following\n      boilerplate notice, with the fields enclosed by brackets \"[]\"\n      replaced with your own identifying information. (Don't include\n      the brackets!)  The text should be enclosed in the appropriate\n      comment syntax for the file format. We also recommend that a\n      file or class name and description of purpose be included on the\n      same \"printed page\" as the copyright notice for easier\n      identification within third-party archives.\n\n   Copyright [yyyy] [name of copyright owner]\n\n   Licensed under the Apache License, Version 2.0 (the \"License\");\n   you may not use this file except in compliance with the License.\n   You may obtain a copy of the License at\n\n       http://www.apache.org/licenses/LICENSE-2.0\n\n   Unless required by applicable law or agreed to in writing, software\n   distributed under the License is distributed on an \"AS IS\" BASIS,\n   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n   See the License for the specific language governing permissions and\n   limitations under the License.\n",
                conditions = listOf(
                    LicenseRule(
                        description = "A copy of the license and copyright notice must be included with the software.",
                        key = "include-copyright",
                        label = "License and copyright notice"
                    ),
                    // ... incomplete
                ),
                description = "A permissive license whose main conditions require preservation of copyright and license notices. Contributors provide an express grant of patent rights. Licensed works, modifications, and larger works may be distributed under different terms and without source code.",
                featured = true,
                hidden = false,
                id = "MDc6TGljZW5zZTI=",
                implementation = "Create a text file (typically named LICENSE or LICENSE.txt) in the root of your source code and copy the text of the license into the file.",
                key = "apache-2.0",
                limitations = listOf(
                    LicenseRule(
                        description = "This license explicitly states that it does NOT grant trademark rights, even though licenses without such a statement probably do not grant any implicit trademark rights.",
                        key = "trademark-use",
                        label = "Trademark use"
                    ),
                    // ... incomplete
                ),
                name = "Apache License 2.0",
                nickname = null,
                permissions = listOf(
                    LicenseRule(
                        description = "This software and derivatives may be used for commercial purposes.",
                        key = "commercial-use",
                        label = "Commercial use"
                    ),
                    // ... incomplete
                ),
                pseudoLicense = false,
                spdxId = "Apache-2.0",
                url = Uri.parse("http://choosealicense.com/licenses/apache-2.0/")
            ),
            lockReason = null,
            mergeCommitAllowed = true,
            mirrorUrl = null,
            name = "PaperPlane",
            nameWithOwner = "TonnyL/PaperPlane",
            openGraphImageUrl = Uri.parse("https://avatars3.githubusercontent.com/u/13329148?s=400&u=0a5724e7c9f7d1cc4a5486ab7ab07abb7b8d7956&v=4"),
            owner = RepositoryOwner(
                avatarUrl = Uri.parse("https://avatars1.githubusercontent.com/u/13329148?u=0a5724e7c9f7d1cc4a5486ab7ab07abb7b8d7956&v=4"),
                id = "MDQ6VXNlcjEzMzI5MTQ4",
                login = "TonnyL",
                resourcePath = Uri.parse("/TonnyL"),
                url = Uri.parse("https://github.com/TonnyL")
            ),
            primaryLanguage = Language(
                color = "#F18E33",
                id = "MDg6TGFuZ3VhZ2UyNzI=",
                name = "Kotlin"
            ),
            projectsResourcePath = Uri.parse("/TonnyL/PaperPlane/projects"),
            projectsUrl = Uri.parse("https://github.com/TonnyL/PaperPlane/projects"),
            pushedAt = Instant.fromEpochMilliseconds(1528288541000),
            rebaseMergeAllowed = true,
            resourcePath = Uri.parse("/TonnyL/PaperPlane"),
            shortDescriptionHTML = "<g-emoji class=\"g-emoji\" alias=\"books\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f4da.png\">📚</g-emoji> PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. ",
            squashMergeAllowed = true,
            sshUrl = "git@github.com:TonnyL/PaperPlane.git",
            updatedAt = Instant.fromEpochMilliseconds(1601370442000),
            url = Uri.parse("https://github.com/TonnyL/PaperPlane"),
            usesCustomOpenGraphImage = false,
            viewerCanAdminister = true,
            viewerCanCreateProjects = true,
            viewerCanSubscribe = true,
            viewerCanUpdateTopics = true,
            viewerHasStarred = false,
            viewerPermission = RepositoryPermission.ADMIN,
            viewerSubscription = SubscriptionState.SUBSCRIBED,
            ownerName = "Li Zhao Tai Lang",
            isViewer = true,
            viewerIsFollowing = false,
            viewerCanFollow = true, // fake value
            forksCount = 286,
            stargazersCount = 1144,
            issuesCount = 27,
            pullRequestsCount = 1,
            watchersCount = 54,
            projectsCount = 0,
            releasesCount = 2,
            branchCount = 1,
            topics = listOf(
                RepositoryTopic(
                    id = "MDE1OlJlcG9zaXRvcnlUb3BpYzY2NDc5Nw==",
                    resourcePath = Uri.parse("/topics/zhihu"),
                    topic = Topic(
                        id = "MDU6VG9waWN6aGlodQ==",
                        name = "zhihu",
                        viewerHasStarred = false
                    ),
                    url = Uri.parse("https://github.com/topics/zhihu")
                ),
                // ... incomplete
            )
        ),
        organizationsRepository = null,
        isFollowing = false,

        onWatchersClicked = {},
        onStargazersClicked = {},
        onForksClicked = {},
        onIssuesClicked = {},
        onPrsClicked = {},
        onProjectsClicked = {},

        readmeResource = Resource.success("<div>\n<g-emoji class=\"g-emoji\" alias=\"books\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f4da.png\">📚</g-emoji> PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. </div>")
    )
}