/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xyu.reader.ui.presenter;

import com.xyu.reader.api.BookApi;
import com.xyu.reader.base.RxPresenter;
import com.xyu.reader.bean.DiscussionList;
import com.xyu.reader.ui.contract.BookDiscussionContract;
import com.xyu.reader.utils.LogUtils;
import com.xyu.reader.utils.RxUtil;
import com.xyu.reader.utils.StringUtils;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author yuyh.
 * @date 16/9/2.
 */
public class BookDiscussionPresenter extends RxPresenter<BookDiscussionContract.View> implements BookDiscussionContract.Presenter {

    private BookApi bookApi;

    @Inject
    public BookDiscussionPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getBookDisscussionList(String block, String sort, String distillate, final int start, int limit) {
        String key = StringUtils.creatAcacheKey("book-discussion-list", block, "all", sort, "all", start + "", limit + "", distillate);

        Observable<DiscussionList> fromNetWork = bookApi.getBookDisscussionList(block, "all", sort, "all", start + "", limit + "", distillate)
                .compose(RxUtil.<DiscussionList>rxCacheListHelper(key));

        //依次检查disk、network
        Subscription rxSubscription = Observable.concat(RxUtil.rxCreateDiskObservable(key, DiscussionList.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DiscussionList>() {
                    @Override
                    public void onCompleted() {
                        mView.complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("getBookDisscussionList:" + e.toString());
                        mView.showError();
                    }

                    @Override
                    public void onNext(DiscussionList list) {
                        boolean isRefresh = start == 0 ? true : false;
                        mView.showBookDisscussionList(list.posts, isRefresh);
                    }
                });
        addSubscrebe(rxSubscription);
    }

}
