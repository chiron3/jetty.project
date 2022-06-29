//
// ========================================================================
// Copyright (c) 1995-2022 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License v. 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.util;

import java.util.concurrent.atomic.AtomicInteger;

public interface Retainable
{
    void retain();

    /**
     * @return true if the buffer was re-pooled, false otherwise.
     */
    boolean release();

    class NonRetaining implements Retainable
    {
        public static final Retainable INSTANCE = new NonRetaining();

        private NonRetaining()
        {
        }

        @Override
        public void retain()
        {
        }

        @Override
        public boolean release()
        {
            return true;
        }
    }

    class Counter implements Retainable
    {
        private final AtomicInteger references = new AtomicInteger(1);

        @Override
        public void retain()
        {
            if (references.getAndUpdate(c -> c == 0 ? 0 : c + 1) == 0)
                throw new IllegalStateException("cannot retain released " + this);
        }

        @Override
        public boolean release()
        {
            int ref = references.updateAndGet(c ->
            {
                if (c == 0)
                    throw new IllegalStateException("already released " + this);
                return c - 1;
            });
            return ref == 0;
        }
    }
}
