package dev.huskuraft.effortless.building.operation.block;

import java.util.List;

import dev.huskuraft.effortless.api.core.ItemStack;
import dev.huskuraft.effortless.building.operation.Operation;
import dev.huskuraft.effortless.building.operation.OperationSummaryType;
import dev.huskuraft.effortless.building.operation.empty.EmptyOperation;

public class BlockInteractOperationResult extends BlockOperationResult {

    public BlockInteractOperationResult(
            BlockInteractOperation operation,
            Type result,
            List<ItemStack> inputs,
            List<ItemStack> outputs
    ) {
        super(operation, result, inputs, outputs);
    }

    @Override
    public Operation getReverseOperation() {
        if (result().fail()) {
            return new EmptyOperation();
        }
        return new EmptyOperation();
    }

    @Override
    public List<ItemStack> getSummary(OperationSummaryType type) {
        return switch (type) {
            case BLOCKS_INTERACTED -> switch (result) {
                case SUCCESS, SUCCESS_PARTIAL, CONSUME -> inputs;
                default -> List.of();
            };
            case BLOCKS_NOT_INTERACTABLE -> switch (result) {
                case FAIL_PLAYER_CANNOT_INTERACT, FAIL_PLAYER_CANNOT_BREAK, FAIL_WORLD_BORDER, FAIL_WORLD_HEIGHT -> inputs;
                default -> List.of();
            };
            case BLOCKS_TOOLS_INSUFFICIENT -> switch (result) {
                case FAIL_TOOL_INSUFFICIENT -> inputs;
                default -> List.of();
            };
            case BLOCKS_BLACKLISTED -> switch (result) {
                case FAIL_CONFIG_BLACKLISTED -> inputs;
                default -> List.of();
            };
            case BLOCKS_NO_PERMISSION -> switch (result) {
                case FAIL_CONFIG_INTERACT_PERMISSION -> inputs;
                default -> List.of();
            };
            default -> List.of();
        };
    }

}
