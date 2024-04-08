/*
 * Copyright (c) 2024 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import hljs from 'highlight.js';

const CODE_REGEX = /(<code class="(.*)">)([\s\S]*?)(<\/code>)/g;

export function useHighlighting() {

  const replaceCodeContent = (originalString: string) => {
    // Use a regular expression to find the code tag and its content.

    let newContent = originalString;
    let match;
    while ((match = CODE_REGEX.exec(originalString)) !== null) {
      let lang = match[2];
      if (lang && lang.startsWith('language-')) {
        lang = lang.substring(9);
      }

      const hljsLang = hljs.getLanguage(lang);
      let highlighted = '';
      if (hljsLang) {
        highlighted = hljs.highlight(lang, match[3]).value;
      } else {
        highlighted = hljs.highlightAuto(match[3]).value;
      }

      newContent = newContent.replace(match[0], `${match[1]}${highlighted}${match[4]}`);
    }

    return newContent;
  };

  return {replaceCodeContent};
}