import {CommonSetEditLogic} from "@components/form/set/CommonSetEditLogic";
import {describe, expect, it} from 'vitest';

describe('CommonSetEditLogic.useLocalizationCollapse', () => {
    it('contains initial localizations', () => {
        const { requiredLocalizations } = CommonSetEditLogic.useLocalizationCollapse(['us']);

        expect(requiredLocalizations.value).toEqual(['us']);
    });
    it('toggle on new locations', () => {
        const { requiredLocalizations, toggleCollapse } = CommonSetEditLogic.useLocalizationCollapse(['us']);

        toggleCollapse('fr', true);
        expect(requiredLocalizations.value).toEqual(['us', 'fr']);
    });
    it('toggle off locations', () => {
        const { requiredLocalizations, toggleCollapse } = CommonSetEditLogic.useLocalizationCollapse(['us']);

        toggleCollapse('us', false);
        expect(requiredLocalizations.value).toEqual([]);
    });
});
