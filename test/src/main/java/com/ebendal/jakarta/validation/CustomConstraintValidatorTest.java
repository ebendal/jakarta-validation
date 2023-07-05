package com.ebendal.jakarta.validation;

import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public abstract class CustomConstraintValidatorTest {

    @Mock
    protected ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderCustomizableContext;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext leafNodeBuilderCustomizableContext;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeContextBuilder nodeContextBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeContextBuilder leafNodeContextBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext nodeBuilderDefinedContext;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderDefinedContext leafNodeBuilderDefinedContext;

    @BeforeEach
    void setUp() {
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        lenient().when(constraintViolationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilderCustomizableContext);
        lenient().when(nodeBuilderCustomizableContext.addPropertyNode(anyString())).thenReturn(nodeBuilderCustomizableContext);
        lenient().when(nodeBuilderCustomizableContext.inIterable()).thenReturn(nodeContextBuilder);
        lenient().when(nodeContextBuilder.atIndex(anyInt())).thenReturn(nodeBuilderDefinedContext);
        lenient().when(nodeBuilderDefinedContext.addPropertyNode(anyString())).thenReturn(nodeBuilderCustomizableContext);
        lenient().when(nodeBuilderCustomizableContext.addBeanNode()).thenReturn(leafNodeBuilderCustomizableContext);
        lenient().when(constraintViolationBuilder.addBeanNode()).thenReturn(leafNodeBuilderCustomizableContext);
        lenient().when(leafNodeBuilderCustomizableContext.inIterable()).thenReturn(leafNodeContextBuilder);
        lenient().when(leafNodeContextBuilder.atIndex(anyInt())).thenReturn(leafNodeBuilderDefinedContext);
    }

    protected void verifyCustomConstraintViolation(String message) {
        verifyCustomConstraintViolationInternal(message);
    }

    protected void verifyCustomConstraintViolation(String message, Node node) {
        verifyCustomConstraintViolationInternal(message, node);
    }

    protected void verifyCustomConstraintViolation(String message, SubPath... subPaths) {
        verifyCustomConstraintViolationInternal(message, Arrays.stream(subPaths).flatMap(subPath -> subPath.getNodes().stream()).toList().toArray(Node[]::new));
    }


    void verifyCustomConstraintViolationInternal(String message, Node... nodes) {
        InOrder inOrder = inOrder(
            context,
            constraintViolationBuilder,
            nodeBuilderCustomizableContext,
            leafNodeBuilderCustomizableContext,
            nodeContextBuilder,
            leafNodeContextBuilder,
            nodeBuilderDefinedContext,
            leafNodeBuilderDefinedContext);
        inOrder.verify(context).disableDefaultConstraintViolation();
        inOrder.verify(context).buildConstraintViolationWithTemplate(message);
        if (nodes.length == 0) {
            inOrder.verify(constraintViolationBuilder).addConstraintViolation();
        } else if (nodes.length == 1) {
            if (nodes[0].isListElement()) {
                inOrder.verify(constraintViolationBuilder).addBeanNode();
                inOrder.verify(leafNodeBuilderCustomizableContext).inIterable();
                inOrder.verify(leafNodeContextBuilder).atIndex(nodes[0].index());
                inOrder.verify(leafNodeBuilderDefinedContext).addConstraintViolation();
            } else {
                inOrder.verify(constraintViolationBuilder).addPropertyNode(nodes[0].name());
                inOrder.verify(nodeBuilderCustomizableContext).addConstraintViolation();
            }
        } else {
            Node previousNode = null;
            Node node = nodes[0];
            for (int i = 1; i < nodes.length; i++) {
                if (node.isListElement()) {
                    inOrder.verify(nodeBuilderCustomizableContext).inIterable();
                    inOrder.verify(nodeContextBuilder).atIndex(node.index());
                } else {
                    if (previousNode == null) {
                        inOrder.verify(constraintViolationBuilder).addPropertyNode(node.name());
                    } else if (previousNode.isListElement()) {
                        inOrder.verify(nodeBuilderDefinedContext).addPropertyNode(node.name());
                    } else {
                        inOrder.verify(nodeBuilderCustomizableContext).addPropertyNode(node.name());
                    }
                }
                previousNode = node;
                node = nodes[i];
            }
            if (node.isListElement()) {
                inOrder.verify(nodeBuilderCustomizableContext).addBeanNode();
                inOrder.verify(leafNodeBuilderCustomizableContext).inIterable();
                inOrder.verify(leafNodeContextBuilder).atIndex(node.index());
                inOrder.verify(leafNodeBuilderDefinedContext).addConstraintViolation();
            } else {
                if (previousNode.isListElement()) {
                    inOrder.verify(nodeBuilderDefinedContext).addPropertyNode(node.name());
                    inOrder.verify(nodeBuilderDefinedContext).addConstraintViolation();
                } else {
                    inOrder.verify(nodeBuilderCustomizableContext).addPropertyNode(node.name());
                    inOrder.verify(nodeBuilderCustomizableContext).addConstraintViolation();
                }
            }
        }
    }

    @Getter(AccessLevel.PACKAGE)
    @Builder(access = AccessLevel.PRIVATE)
    protected static class SubPath {
        private final List<Node> nodes;
    }

    interface Node {

        private boolean isListElement() {
            return this instanceof ListElement;
        }

        default String name() {
            return null;
        }

        default Integer index() {
            return null;
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    private record Field(@NonNull String name) implements Node {

    }

    @Builder(access = AccessLevel.PRIVATE)
    protected record ListElement(@NonNull Integer index) implements Node {

    }

    protected static SubPath subPath(String name) {
        return SubPath.builder()
            .nodes(List.of(Field.builder()
                .name(name)
                .build()))
            .build();
    }

    protected static SubPath subPath(String name, int index) {
        return SubPath.builder()
            .nodes(List.of(Field.builder()
                    .name(name)
                    .build(),
                listElement(index)))
            .build();
    }


    protected static ListElement listElement(int index) {
        return ListElement.builder()
            .index(index)
            .build();
    }
}
